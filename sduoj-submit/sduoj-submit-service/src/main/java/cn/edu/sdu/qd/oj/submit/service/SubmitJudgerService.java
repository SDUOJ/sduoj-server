/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.submit.client.ProblemClient;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionJudgeConverter;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionResultConverter;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionUpdateConverter;
import cn.edu.sdu.qd.oj.submit.dao.SubmissionDao;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionResultDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionUpdateReqDTO;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionJudgeDTO;
import cn.edu.sdu.qd.oj.submit.enums.SubmissionJudgeResult;
import cn.edu.sdu.qd.oj.submit.util.RabbitSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @ClassName SubmitJudgerService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:49
 * @Version V1.0
 **/

@Service
@Slf4j
public class SubmitJudgerService {
    @Autowired
    private SubmissionDao submissionDao;

    @Autowired
    private SubmissionJudgeConverter submissionJudgeConverter;

    @Autowired
    private SubmissionUpdateConverter submissionUpdateConverter;

    @Autowired
    private SubmissionResultConverter submissionResultConverter;

    @Autowired
    private ProblemClient problemClient;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RabbitSender rabbitSender;

    /**
    * @return cn.edu.sdu.qd.oj.submit.dto.SubmissionJudgeDTO     Nullable
    **/
    public SubmissionJudgeDTO query(long submissionId, int version) {
        SubmissionDO submissionJudgeDO = submissionDao.lambdaQuery().select(
                SubmissionDO::getSubmissionId,
                SubmissionDO::getProblemId,
                SubmissionDO::getUserId,
                SubmissionDO::getJudgeTemplateId,
                SubmissionDO::getZipFileId,
                SubmissionDO::getGmtCreate,
                SubmissionDO::getCode,
                SubmissionDO::getCodeLength,
                SubmissionDO::getVersion
        ).eq(SubmissionDO::getSubmissionId, submissionId)
         .eq(SubmissionDO::getVersion, version)
         .one();
        if (submissionJudgeDO != null) {
            boolean succ = submissionDao.lambdaUpdate()
                    .set(SubmissionDO::getJudgeResult, SubmissionJudgeResult.JUDGING.code)
                    .eq(SubmissionDO::getSubmissionId, submissionId)
                    .update();
            if (!succ) {
                return null;
            }
        }
        return submissionJudgeConverter.to(submissionJudgeDO);
    }

    @Transactional
    public void updateSubmission(SubmissionUpdateReqDTO reqDTO) {
        SubmissionDO submissionDO = submissionUpdateConverter.from(reqDTO);
        AssertUtils.isTrue(submissionDao.updateById(submissionDO), ApiExceptionEnum.UNKNOWN_ERROR);

        // 查出 user、problem、contest 信息，同步推送过题消息
        submissionDO = submissionDao.lambdaQuery().select(
                SubmissionDO::getSubmissionId,
                SubmissionDO::getGmtCreate,
                SubmissionDO::getUserId,
                SubmissionDO::getContestId,
                SubmissionDO::getProblemId,
                SubmissionDO::getJudgeScore,
                SubmissionDO::getJudgeResult
        ).eq(SubmissionDO::getSubmissionId, reqDTO.getSubmissionId()).one();
        if (SubmissionJudgeResult.AC.equals(submissionDO.getJudgeResult())) {
            String key;
            String problemCode = problemClient.problemIdToProblemCode(submissionDO.getProblemId());
            // 用户过题
            key = RedisConstants.getUserACProblem(submissionDO.getContestId(), submissionDO.getUserId());
            if (redisUtils.hasKey(key)) {
                redisUtils.sSet(key, problemCode);
            }
            // 比赛过题
            key = RedisConstants.getContestSubmission(submissionDO.getContestId());
            if (redisUtils.hasKey(key)) {
                redisUtils.hincr(key, RedisConstants.getContestProblemAccept(problemCode), 1);
            }
            // 过题消息
            SubmissionResultDTO submissionResultDTO = submissionResultConverter.to(submissionDO);
            submissionResultDTO.setProblemCode(problemCode);
            rabbitSender.sendACMessage(submissionResultDTO);
        }
    }
}