/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.service;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.contest.client.ProblemClient;
import cn.edu.sdu.qd.oj.contest.client.SubmissionClient;
import cn.edu.sdu.qd.oj.contest.client.UserClient;
import cn.edu.sdu.qd.oj.contest.converter.ContestConvertUtils;
import cn.edu.sdu.qd.oj.contest.converter.ContestConverter;
import cn.edu.sdu.qd.oj.contest.converter.ContestListConverter;
import cn.edu.sdu.qd.oj.contest.dao.ContestDao;
import cn.edu.sdu.qd.oj.contest.dao.ContestListDao;
import cn.edu.sdu.qd.oj.contest.dto.*;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;
import cn.edu.sdu.qd.oj.contest.entity.ContestListDO;
import cn.edu.sdu.qd.oj.contest.enums.ContestTypeEnum;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDescriptionDTO;
import cn.edu.sdu.qd.oj.submit.dto.*;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionCreateReqDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListReqDTO;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContestService {

    @Autowired
    private ContestDao contestDao;

    @Autowired
    private ContestListDao contestListDao;

    @Autowired
    private ContestConverter contestConverter;

    @Autowired
    private ContestListConverter contestListConverter;

    @Autowired
    private ProblemClient problemClient;

    @Autowired
    private SubmissionClient submissionClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisUtils redisUtils;

    public ContestDTO queryAndValidate(Long contestId, long userId) {
        ContestDO contestDO = contestDao.getById(contestId);
        AssertUtils.notNull(contestDO, ApiExceptionEnum.CONTEST_NOT_FOUND);

        // 鉴权  TODO: feature 能力架构设计，解耦+去魔法值
        Map<String, String> featureMap = ContestConvertUtils.stringToMap(contestDO.getFeatures());
        String openness = Optional.ofNullable(featureMap).map(map -> map.get("openness")).orElse("");
        if (!contestDO.containsUserIdInParticipants(userId) && "private".equals(openness)) {
            contestDO.setProblems(null);
            contestDO.setMarkdownDescription(null);
        }
        // 比赛未开始无法查题
        if (contestDO.getGmtStart().after(new Date())) {
            contestDO.setProblems(null);
        }

        return contestConverter.to(contestDO);
    }

    @Transactional
    public ContestDTO query(long contestId, long userId) {
        ContestDTO contestDTO = queryAndValidate(contestId, userId);

        // 查询各题的提交情况  缓存优先
        String key = RedisConstants.getContestSubmission(contestId);
        if (redisUtils.hasKey(key)) {
            contestDTO.getProblems().forEach(p -> {
                p.setAcceptNum((int) redisUtils.hget(key, RedisConstants.getContestProblemAccept(p.getProblemCode())));
                p.setSubmitNum((int) redisUtils.hget(key, RedisConstants.getContestProblemSubmit(p.getProblemCode())));
            });
        } else {
            Map<String, ProblemListDTO> problemCodeToProblemListDTOMap = submissionClient.queryContestSubmitAndAccept(contestId)
                    .stream().collect(Collectors.toMap(ProblemListDTO::getProblemCode, Function.identity(), (k1, k2) -> k1));
            contestDTO.getProblems().forEach(p -> {
                Optional.ofNullable(problemCodeToProblemListDTOMap.get(p.getProblemCode())).ifPresent(m -> {
                    p.setAcceptNum(m.getAcceptNum());
                    p.setSubmitNum(m.getSubmitNum());
                });
                redisUtils.hset(key, RedisConstants.getContestProblemAccept(p.getProblemCode()), p.getAcceptNum());
                redisUtils.hset(key, RedisConstants.getContestProblemSubmit(p.getProblemCode()), p.getSubmitNum());
            });
            redisUtils.expire(key, RedisConstants.CONTEST_SUBMISSION_NUM_EXPIRE);
        }

        return contestDTO;
    }


    @Transactional
    public void participate(Long contestId, Long userId, String password) {
        ContestDO contestDO = contestDao.lambdaQuery().select(
                ContestDO::getContestId,
                ContestDO::getFeatures,
                ContestDO::getPassword,
                ContestDO::getVersion,
                ContestDO::getParticipants
        ).eq(ContestDO::getContestId, contestId).one();
        AssertUtils.notNull(contestDO, ApiExceptionEnum.CONTEST_NOT_FOUND);

        // TODO: feature 能力架构设计，解耦+去魔法值
        Map<String, String> featureMap = ContestConvertUtils.stringToMap(contestDO.getFeatures());
        String openness = Optional.ofNullable(featureMap).map(map -> map.get("openness")).orElse("");
        switch (openness) {
            case "public":
                break;
            case "protected":
            case "private":
                AssertUtils.isTrue(contestDO.getPassword().equals(password), ApiExceptionEnum.CONTEST_PASSWORD_NOT_MATCHING);
                break;
        }

        // 新增一个用户到比赛
        AssertUtils.isTrue(contestDO.addOneParticipant(userId), ApiExceptionEnum.CONTEST_HAD_PARTICIPATED);
        // 密码不进行更改
        contestDO.setPassword(null);

        AssertUtils.isTrue(contestDao.updateById(contestDO), ApiExceptionEnum.SERVER_BUSY); // 此时乐观锁会自动填入

        userClient.addUserParticipateContest(userId, contestId);
    }

    public PageResult<ContestListDTO> page(ContestListReqDTO reqDTO) {
        LambdaQueryChainWrapper<ContestListDO> query = contestListDao.lambdaQuery()
                .orderByDesc(ContestListDO::getGmtStart);

        // TODO: 修改掉临时的暴力 feature 匹配
        Optional.ofNullable(reqDTO).map(ContestListReqDTO::getMode).filter(StringUtils::isNotBlank).ifPresent(mode -> {
            query.like(ContestListDO::getFeatures, "mode:" + mode);
        });

        Page<ContestListDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        return new PageResult<>(pageResult.getPages(), contestListConverter.to(pageResult.getRecords()));
    }

    public ContestListDTO queryUpcomingContest() {
        ContestListDO contestListDO = contestListDao.lambdaQuery()
                .orderByAsc(ContestListDO::getGmtStart)
                .ge(ContestListDO::getGmtStart, new Date()).last("limit 1").one();
        return contestListConverter.to(contestListDO);
    }

    public ContestProblemDTO queryProblem(long contestId, int problemIndex, long userId) {
        ContestDO contestDO = queryContestAndValidate(contestId, userId);
        ContestProblemListDTO contestProblemListDTO = contestDO.getProblemCodeByIndex(problemIndex);
        // RPC查询题目
        ProblemDTO problemDTO = problemClient.queryAndValidate(contestProblemListDTO.getProblemCode(), contestProblemListDTO.getProblemDescriptionId(), userId);
        // 类型转换
        ProblemDescriptionDTO problemDescriptionDTO = problemDTO.getProblemDescriptionDTO();
        ContestProblemDescriptionDTO contestProblemDescriptionDTO = ContestProblemDescriptionDTO.builder()
                .htmlDescription(problemDescriptionDTO.getHtmlDescription())
                .htmlHint(problemDescriptionDTO.getHtmlHint())
                .htmlInput(problemDescriptionDTO.getHtmlInput())
                .htmlOutput(problemDescriptionDTO.getHtmlOutput())
                .htmlSampleInput(problemDescriptionDTO.getHtmlSampleInput())
                .htmlSampleOutout(problemDescriptionDTO.getHtmlSampleOutout())
                .markdownDescription(problemDescriptionDTO.getMarkdownDescription())
                .build();
        ContestProblemDTO contestProblemDTO = ContestProblemDTO.builder()
                .languages(problemDTO.getLanguages())
                .judgeTemplates(problemDTO.getJudgeTemplates())
                .timeLimit(problemDTO.getTimeLimit())
                .memoryLimit(problemDTO.getMemoryLimit())
                .problemCode(problemDTO.getProblemCode())
                .problemCaseDTOList(problemDTO.getProblemCaseDTOList())
                .problemTitle(contestProblemListDTO.getProblemTitle())
                .problemWeight(contestProblemListDTO.getProblemWeight())
                .problemDescriptionDTO(contestProblemDescriptionDTO)
                .build();
        return contestProblemDTO;
    }

    private ContestDO queryContestAndValidate(long contestId, long userId) {
        ContestDO contestDO = contestDao.lambdaQuery().select(
                ContestDO::getFeatures,
                ContestDO::getGmtStart,
                ContestDO::getGmtEnd,
                ContestDO::getProblems,
                ContestDO::getParticipants
        ).eq(ContestDO::getContestId, contestId).one();
        AssertUtils.notNull(contestDO, ApiExceptionEnum.CONTEST_NOT_FOUND);

        // TODO: feature 能力架构设计，解耦+去魔法值
        Map<String, String> featureMap = ContestConvertUtils.stringToMap(contestDO.getFeatures());
        String openness = Optional.ofNullable(featureMap).map(map -> map.get("openness")).orElse("");

        AssertUtils.isTrue(contestDO.containsUserIdInParticipants(userId) || !"private".equals(openness), ApiExceptionEnum.CONTEST_NOT_PARTICIPATE);

        if (contestDO.getGmtStart().after(new Date())) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_BEGIN);
        }
        return contestDO;
    }


    public String createSubmission(ContestSubmissionCreateReqDTO reqDTO) {
        ContestDO contestDO = queryContestAndValidate(reqDTO.getContestId(), reqDTO.getUserId());

        // 未登记参加比赛不能做提交    TODO: 更好的设计方式
        AssertUtils.isTrue(contestDO.containsUserIdInParticipants(reqDTO.getUserId()), ApiExceptionEnum.CONTEST_NOT_PARTICIPATE);
        // 比赛未开始不能提交
        if (contestDO.getGmtStart().after(new Date())) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_BEGIN);
        }

        ContestProblemListDTO contestProblemListDTO = contestDO.getProblemCodeByIndex(reqDTO.getProblemIndex());
        String problemCode = contestProblemListDTO.getProblemCode();
        SubmissionCreateReqDTO submissionCreateReqDTO = SubmissionCreateReqDTO.builder()
                .problemCode(problemCode)
                .language(reqDTO.getLanguage())
                .judgeTemplateId(reqDTO.getJudgeTemplateId())
                .code(reqDTO.getCode())
                .userId(reqDTO.getUserId())
                .ipv4(reqDTO.getIpv4())
                .build();
        long submissionId = submissionClient.create(reqDTO.getContestId(), submissionCreateReqDTO);
        return Long.toHexString(submissionId);
    }

    public PageResult<ContestSubmissionListDTO> listSubmission(ContestSubmissionListReqDTO reqDTO, Long requestUserId) {
        ContestDO contestDO = queryContestAndValidate(reqDTO.getContestId(), requestUserId);
        // TODO: 判定比赛是否不支持查询他人提交 / 判定比赛是否赛时不支持查询他人提交



        // 构造 problemCode To ProblemIndex Map
        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        List<String> problemCodeList = contestProblemListDTOList.stream().map(ContestProblemListDTO::getProblemCode).collect(Collectors.toList());
        Map<String, Integer> problemCodeToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemCodeToProblemIndexMap.put(contestProblemListDTOList.get(i).getProblemCode(), i + 1);
        }

        ContestProblemListDTO contestProblemListDTO = contestDO.getProblemCodeByIndex(reqDTO.getProblemIndex());
        String problemCode = Optional.ofNullable(contestProblemListDTO).map(ContestProblemListDTO::getProblemCode).orElse(null);

        SubmissionListReqDTO submissionListReqDTO = SubmissionListReqDTO.builder()
                .pageNow(reqDTO.getPageNow())
                .pageSize(reqDTO.getPageSize())
                .sortBy(reqDTO.getSortBy())
                .ascending(reqDTO.getAscending())
                .judgeResult(reqDTO.getJudgeResult())
                .judgeTemplateId(reqDTO.getJudgeTemplateId())
                .language(reqDTO.getLanguage())
                .problemCode(problemCode)
                .username(reqDTO.getUsername())
                .problemCodeList(problemCodeList)
                .build();
        try {
            PageResult<SubmissionListDTO> pageResult = submissionClient.page(reqDTO.getContestId(), submissionListReqDTO);
            List<ContestSubmissionListDTO> contestSubmissionListDTOList = pageResult.getRows().stream().map(submissionListDTO -> {
                Integer problemIndex = problemCodeToProblemIndexMap.get(submissionListDTO.getProblemCode());
                ContestSubmissionListDTO contestSubmissionListDTO = new ContestSubmissionListDTO();
                BeanUtils.copyProperties(submissionListDTO, contestSubmissionListDTO);
                contestSubmissionListDTO.setProblemCode(String.valueOf(problemIndex));
                contestSubmissionListDTO.setProblemTitle(contestProblemListDTOList.get(problemIndex - 1).getProblemTitle());
                return contestSubmissionListDTO;
            }).collect(Collectors.toList());
            return new PageResult<>(pageResult.getTotalPage(), contestSubmissionListDTOList);
        } catch (InternalApiException exception) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
    }

    public List<String> queryACProblem(Long userId, long contestId) {
        ContestDO contestDO = queryContestAndValidate(contestId, userId);
        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        Map<String, String> problemCodeToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemCodeToProblemIndexMap.put(contestProblemListDTOList.get(i).getProblemCode(), String.valueOf(i + 1));
        }
        List<String> problemCodeList = Optional.ofNullable(submissionClient.queryACProblem(userId, contestId)).orElse(Lists.newArrayList());
        return problemCodeList.stream().map(problemCodeToProblemIndexMap::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public SubmissionDTO querySubmission(Long submissionId, long contestId, Long userId) throws InternalApiException {
        SubmissionDTO submissionDTO = submissionClient.query(submissionId, contestId);
        if (submissionDTO == null) {
            return null;
        }

        ContestDO contestDO = queryContestAndValidate(contestId, userId);

        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        Map<Long, Integer> problemIdToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemIdToProblemIndexMap.put(problemClient.problemCodeToProblemId(contestProblemListDTOList.get(i).getProblemCode()), i + 1);
        }

        // problemId、problemCode 脱敏
        submissionDTO.setProblemCode(problemIdToProblemIndexMap.get(submissionDTO.getProblemId()).toString());
        submissionDTO.setProblemId(null);

        // code 脱敏      TODO: 管理员可以看所有代码
        if (!submissionDTO.getUserId().equals(userId)) {
            submissionDTO.setCode(null);
            submissionDTO.setCheckpointResults(null);
        }
        return submissionDTO;
    }

    public List<ContestRankDTO> queryRank(long contestId, UserSessionDTO userSessionDTO) throws InternalApiException {
        // 查比赛
        ContestDO contestDO = queryContestAndValidate(contestId, userSessionDTO.getUserId());

        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        Map<Long, Integer> problemIdToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemIdToProblemIndexMap.put(problemClient.problemCodeToProblemId(contestProblemListDTOList.get(i).getProblemCode()), i + 1);
        }

        // 比赛 featureMap
        Map<String, String> featureMap = Optional.ofNullable(ContestConvertUtils.stringToMap(contestDO.getFeatures())).orElse(Maps.newHashMap());

        // 查提交
        List<SubmissionResultDTO> submissionResultDTOList = submissionClient.listResult(contestId);
        // problemCode 脱敏
        submissionResultDTOList = submissionResultDTOList.stream()
                .peek(o -> o.setProblemCode(Optional.ofNullable(o.getProblemId())
                                                    .map(problemIdToProblemIndexMap::get)
                                                    .map(Long::toString)
                                                    .orElse(null)))
                .filter(o -> Objects.nonNull(o.getProblemCode()))
                .collect(Collectors.toList());

        Map<Long, List<SubmissionResultDTO>> userIdToSubmissionListMap = submissionResultDTOList.stream().collect(Collectors.groupingBy(SubmissionResultDTO::getUserId));
        // 转换
        List<ContestRankDTO> contestRankDTOList = ContestRankDTO.create(userIdToSubmissionListMap, contestProblemListDTOList.size());
        // TODO: 挂星选手


        // 置入 username 数据
        contestRankDTOList.forEach(o -> o.setUsername(userClient.userIdToUsername(o.getUserId())));

        // 比赛中时需要进行 submissions 转 problemResults
        if (contestDO.getGmtEnd().after(new Date())) {
            ContestTypeEnum contestType = ContestTypeEnum.of(featureMap.get("mode"));
            contestRankDTOList.forEach(o -> o.computeProblemResults(contestType));
        }

        return contestRankDTOList;
    }
}