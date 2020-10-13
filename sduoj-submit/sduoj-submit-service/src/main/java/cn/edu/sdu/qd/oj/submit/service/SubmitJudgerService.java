/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.submit.client.UserClient;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionConverter;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionJudgeConverter;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionUpdateConverter;
import cn.edu.sdu.qd.oj.submit.dao.SubmissionDao;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionUpdateReqDTO;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionJudgeDTO;
import cn.edu.sdu.qd.oj.submit.enums.SubmissionJudgeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private SubmissionConverter submissionConverter;

    @Autowired
    private SubmissionUpdateConverter submissionUpdateConverter;

    @Autowired
    private UserClient userClient;

    public SubmissionJudgeDTO query(long submissionId) {
        SubmissionDO submissionJudgeDO = submissionDao.lambdaQuery().select(
                SubmissionDO::getSubmissionId,
                SubmissionDO::getProblemId,
                SubmissionDO::getUserId,
                SubmissionDO::getLanguage,
                SubmissionDO::getGmtCreate,
                SubmissionDO::getCode,
                SubmissionDO::getCodeLength
        ).eq(SubmissionDO::getSubmissionId, submissionId).one();
        AssertUtils.notNull(submissionJudgeDO, ApiExceptionEnum.SUBMISSION_NOT_FOUND);
        return submissionJudgeConverter.to(submissionJudgeDO);
    }

    @Transactional
    public void updateSubmission(SubmissionUpdateReqDTO reqDTO) {
        SubmissionDO submissionDO = submissionUpdateConverter.from(reqDTO);
        AssertUtils.isTrue(submissionDao.updateById(submissionDO), ApiExceptionEnum.UNKNOWN_ERROR);
        // 查出 user、problem、contest 信息，同步推送过题消息  TODO: 异步
        submissionDO = submissionDao.lambdaQuery().select(
                SubmissionDO::getSubmissionId,
                SubmissionDO::getUserId,
                SubmissionDO::getContestId,
                SubmissionDO::getProblemId,
                SubmissionDO::getJudgeResult
        ).eq(SubmissionDO::getSubmissionId, reqDTO.getSubmissionId()).one();
        if (SubmissionJudgeResult.AC.equals(submissionDO.getJudgeResult())) {
            userClient.addUserACProblem(submissionDO.getUserId(), submissionDO.getContestId(), submissionDO.getProblemId());
        }
    }
}