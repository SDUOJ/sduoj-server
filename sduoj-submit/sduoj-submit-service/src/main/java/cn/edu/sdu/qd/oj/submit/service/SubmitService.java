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

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.*;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.submit.client.JudgeTemplateClient;
import cn.edu.sdu.qd.oj.submit.client.ProblemClient;
import cn.edu.sdu.qd.oj.submit.client.UserClient;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionConverter;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionListConverter;
import cn.edu.sdu.qd.oj.submit.converter.SubmissionResultConverter;
import cn.edu.sdu.qd.oj.submit.dao.SubmissionDao;
import cn.edu.sdu.qd.oj.submit.dto.*;
import cn.edu.sdu.qd.oj.submit.entity.SubmissionDO;
import cn.edu.sdu.qd.oj.submit.enums.SubmissionJudgeResult;
import cn.edu.sdu.qd.oj.submit.util.RabbitSender;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName SubmitService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/6 16:04
 * @Version V1.0
 **/

@Service
@Slf4j
public class SubmitService {

    @Autowired
    private SubmissionDao submissionDao;

    @Autowired
    private UserClient userClient;

    @Autowired
    private SubmissionConverter submissionConverter;

    @Autowired
    private SubmissionListConverter submissionListConverter;

    @Autowired
    private SubmissionResultConverter submissionResultConverter;

    @Autowired
    private RedisUtils redisUtils;

    // TODO: 临时采用 IP+PID 格式, 生产时加配置文件 Autowired
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Autowired
    private ProblemClient problemClient;

    @Autowired
    private JudgeTemplateClient judgeTemplateClient;

    @Autowired
    private RabbitSender rabbitSender;


    /**
    * @Description 提交返回提交id
    * @param submissionUpdateReqDTO
    * @param contestId 0代表非比赛提交
    * @return java.lang.Long
    **/
    @Transactional
    public Long createSubmission(SubmissionCreateReqDTO submissionUpdateReqDTO, long contestId) {
        // TODO: 校验提交语言支持、校验题目对用户权限

        Long problemId = problemClient.problemCodeToProblemId(submissionUpdateReqDTO.getProblemCode());
        AssertUtils.notNull(problemId, ApiExceptionEnum.PROBLEM_NOT_FOUND);

        long snowflaskId = snowflakeIdWorker.nextId();
        SubmissionDO submissionDO = SubmissionDO.builder()
                .submissionId(snowflaskId)
                .code(submissionUpdateReqDTO.getCode())
                .ipv4(submissionUpdateReqDTO.getIpv4())
                .codeLength(submissionUpdateReqDTO.getCode().length())
                .judgeTemplateId(submissionUpdateReqDTO.getJudgeTemplateId())
                .language(submissionUpdateReqDTO.getLanguage())
                .problemId(problemId)
                .userId(submissionUpdateReqDTO.getUserId())
                .contestId(contestId)
                .build();
        AssertUtils.isTrue(submissionDao.save(submissionDO), ApiExceptionEnum.UNKNOWN_ERROR);
        if (contestId != 0) {
            // 比赛过题
            String problemCode = problemClient.problemIdToProblemCode(submissionDO.getProblemId());
            String key = RedisConstants.getContestSubmission(contestId);
            if (redisUtils.hasKey(key)) {
                redisUtils.hincr(key, RedisConstants.getContestProblemSubmit(problemCode), 1);
            }
        }
        // 发送评测请求
        SubmissionMessageDTO messageDTO = SubmissionMessageDTO.builder()
                .code(submissionDO.getCode())
                .codeLength(submissionDO.getCodeLength())
                .gmtCreate(submissionDO.getGmtCreate())
                .judgeTemplateId(submissionDO.getJudgeTemplateId())
                .zipFileId(submissionDO.getZipFileId())
                .submissionId(submissionDO.getSubmissionId())
                .problemId(submissionDO.getProblemId())
                .version(submissionDO.getVersion())
                .userId(submissionDO.getUserId())
                .build();
        if (!rabbitSender.sendJudgeRequest(messageDTO)) {
            log.error("[submit] submissionCreate MQ send error {}", messageDTO.getSubmissionId());
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        return submissionDO.getSubmissionId();
    }

    /*
    * @Description 按submissionId查询提交
    * @param submissionId
    * @param contestId 0 表示查非比赛提交
    * @return cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO
    **/
    public SubmissionDTO queryById(long submissionId, long contestId) {
        SubmissionDO submissionDO = submissionDao.lambdaQuery()
                .eq(SubmissionDO::getContestId, contestId)
                .eq(SubmissionDO::getSubmissionId, submissionId)
                .one();
        AssertUtils.notNull(submissionDO, ApiExceptionEnum.SUBMISSION_NOT_FOUND);
        SubmissionDTO submissionDTO = submissionConverter.to(submissionDO);
        submissionDTO.setCheckpointNum(problemClient.problemIdToProblemCheckpointNum(submissionDTO.getProblemId()));
        submissionDTO.setUsername(userClient.userIdToUsername(submissionDTO.getUserId()));
        submissionDTO.setProblemCode(problemClient.problemIdToProblemCode(submissionDTO.getProblemId()));
        submissionDTO.setJudgeTemplateTitle(judgeTemplateClient.idToTitle(submissionDTO.getJudgeTemplateId()));
        submissionDTO.setJudgeTemplateType(judgeTemplateClient.idToType(submissionDTO.getJudgeTemplateId()));
        submissionDTO.setProblemTitle(problemClient.problemIdToProblemTitle(submissionDTO.getProblemId()));
        return submissionDTO;
    }

    /**
    * @Description 分页查询提交
    * @param reqDTO
    * @param contestId 0 表示查非比赛提交
    * @param userSessionDTO
     * @return cn.edu.sdu.qd.oj.common.entity.PageResult<cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO>
    **/
    public PageResult<SubmissionListDTO> querySubmissionByPage(SubmissionListReqDTO reqDTO,
                                                               long contestId,
                                                               UserSessionDTO userSessionDTO) throws InternalApiException {
        // 填充字段
        if (StringUtils.isNotBlank(reqDTO.getUsername())) {
            Long userId = userClient.usernameToUserId(reqDTO.getUsername());
            if (userId == null) {
                return new PageResult<>();
            }
            reqDTO.setUserId(userId);
        }
        if (StringUtils.isNotBlank(reqDTO.getProblemCode())) {
            Long problemId = problemClient.problemCodeToProblemId(reqDTO.getProblemCode());
            if (problemId == null) {
                return new PageResult<>();
            }
            reqDTO.setProblemId(problemId);
        }

        // 构建查询列
        LambdaQueryChainWrapper<SubmissionDO> query = submissionDao.lambdaQuery().select(
            SubmissionDO::getSubmissionId,
            SubmissionDO::getProblemId,
            SubmissionDO::getUserId,
            SubmissionDO::getLanguage,
            SubmissionDO::getJudgeTemplateId,
            SubmissionDO::getGmtCreate,
            SubmissionDO::getGmtModified,
            SubmissionDO::getIsPublic,
            SubmissionDO::getValid,
            SubmissionDO::getJudgeResult,
            SubmissionDO::getJudgeScore,
            SubmissionDO::getUsedTime,
            SubmissionDO::getUsedMemory,
            SubmissionDO::getCodeLength
        ).eq(SubmissionDO::getContestId, contestId);

        // 排序字段
        String orderBy = reqDTO.getOrderBy();
        if (orderBy != null) {
            switch (reqDTO.getOrderBy()) {
                case "usedTime":
                    query.orderBy(true, reqDTO.getAscending(), SubmissionDO::getUsedTime);
                    break;
                case "usedMemory":
                    query.orderBy(true, reqDTO.getAscending(), SubmissionDO::getUsedMemory);
                    break;
                case "gmtCreate":
                    query.orderBy(true, reqDTO.getAscending(), SubmissionDO::getGmtCreate);
                    break;
            }
        } else {
            // 默认按时间排序
            query.orderByDesc(SubmissionDO::getGmtCreate);
        }

        // 等值字段
        Optional.of(reqDTO).map(SubmissionListReqDTO::getLanguage).filter(StringUtils::isNotBlank).ifPresent(language -> {
            query.eq(SubmissionDO::getLanguage, language);
        });
        Optional.of(reqDTO).map(SubmissionListReqDTO::getJudgeResult).ifPresent(judgeResult -> {
            query.eq(SubmissionDO::getJudgeResult, judgeResult);
        });
        Optional.of(reqDTO).map(SubmissionListReqDTO::getUserId).ifPresent(userId -> {
            query.eq(SubmissionDO::getUserId, userId);
        });
        Optional.of(reqDTO).map(SubmissionListReqDTO::getProblemId).ifPresent(problemId -> {
            query.eq(SubmissionDO::getProblemId, problemId);
        });
        Optional.of(reqDTO).map(SubmissionListReqDTO::getProblemCodeList).ifPresent(problemCodeList -> {
            List<Long> problemIdList = problemCodeList.stream().map(problemClient::problemCodeToProblemId).collect(Collectors.toList());
            query.in(SubmissionDO::getProblemId, problemIdList);
        });

        // 过滤不 public 的题目
        List<Long> problemIdList = problemClient.queryPrivateProblemIdList(Optional.ofNullable(userSessionDTO).map(UserSessionDTO::getUserId).orElse(null));
        if (!CollectionUtils.isEmpty(problemIdList)) {
            query.notIn(SubmissionDO::getProblemId, problemIdList);
        }

        // 查询数据
        Page<SubmissionDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        List<SubmissionListDTO> submissionListDTOList = submissionListConverter.to(pageResult.getRecords());

        // 置 problemCode
        if (StringUtils.isNotBlank(reqDTO.getProblemCode())) {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemCode(reqDTO.getProblemCode()));
        } else {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemCode(problemClient.problemIdToProblemCode(submissionListDTO.getProblemId())));
        }
        // 置 username
        if (reqDTO.getUserId() != null) {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setUsername(reqDTO.getUsername()));
        } else {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setUsername(userClient.userIdToUsername(submissionListDTO.getUserId())));
        }
        // 置题目标题
        if (reqDTO.getProblemId() != null) {
            String problemTitle = problemClient.problemIdToProblemTitle(reqDTO.getProblemId());
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemTitle(problemTitle));
        } else {
            submissionListDTOList.forEach(submissionListDTO -> submissionListDTO.setProblemTitle(problemClient.problemIdToProblemTitle(submissionListDTO.getProblemId())));
        }
        // 置 judgeTemplateTitle
        submissionListDTOList.forEach(submissionListDTO ->
            submissionListDTO.setJudgeTemplateTitle(judgeTemplateClient.idToTitle(submissionListDTO.getJudgeTemplateId())
        ));

        return new PageResult<>(pageResult.getPages(), submissionListDTOList);
    }

    public List<SubmissionResultDTO> listResult(long contestId) {
        List<SubmissionDO> list = submissionDao.lambdaQuery().select(
                SubmissionDO::getSubmissionId,
                SubmissionDO::getGmtCreate,
                SubmissionDO::getProblemId,
                SubmissionDO::getUserId,
                SubmissionDO::getJudgeScore,
                SubmissionDO::getJudgeResult
        ).eq(SubmissionDO::getContestId, contestId).list();
        List<SubmissionResultDTO> submissionResultDTOList = submissionResultConverter.to(list);
        submissionResultDTOList.forEach(o -> o.setProblemCode(problemClient.problemIdToProblemCode(o.getProblemId())));
        return submissionResultDTOList;
    }

    public List<String> queryACProblem(long userId, long contestId) {
        String key = RedisConstants.getUserACProblem(contestId, userId);
        if (redisUtils.hasKey(key)) {
            return redisUtils.sGet(key).stream().map(o -> (String) o).collect(Collectors.toList());
        }
        List<SubmissionDO> submissionDOList = submissionDao.lambdaQuery()
                .select(SubmissionDO::getProblemId)
                .eq(SubmissionDO::getContestId, contestId)
                .eq(SubmissionDO::getUserId, userId)
                .eq(SubmissionDO::getJudgeResult, SubmissionJudgeResult.AC.code)
                .groupBy(SubmissionDO::getProblemId)
                .list();
        List<String> problemCodeList = submissionDOList.stream().map(SubmissionDO::getProblemId).map(problemClient::problemIdToProblemCode).collect(Collectors.toList());
        boolean succ = redisUtils.sSetAndTime(key, RedisConstants.ACPROBLEM_EXPIRE, problemCodeList.toArray()) == problemCodeList.size();
        if (!succ) {
           log.error("addACProblem Error! {} {}", contestId, userId);
        }
        return problemCodeList;
    }

    public List<ProblemListDTO> queryContestSubmitAndAccept(long contestId) {
        List<SubmissionDO> submissionDOList = submissionDao.lambdaQuery()
                .select(SubmissionDO::getProblemId, SubmissionDO::getJudgeResult)
                .eq(SubmissionDO::getContestId, contestId)
                .list();
        List<ProblemListDTO> problemListDTOList = new ArrayList<>();
        submissionDOList.stream().collect(Collectors.groupingBy(SubmissionDO::getProblemId)).forEach((problemId, submissionList) -> {
            long acceptNum = submissionList.stream().filter(s -> SubmissionJudgeResult.AC.code == s.getJudgeResult()).count();
            problemListDTOList.add(ProblemListDTO.builder()
                    .problemId(problemId)
                    .problemCode(problemClient.problemIdToProblemCode(problemId))
                    .acceptNum((int) acceptNum)
                    .submitNum(submissionList.size())
                    .build());
        });
        return problemListDTOList;
    }
}