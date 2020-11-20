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

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.contest.cache.ContestCacheTypeManager;
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
import cn.edu.sdu.qd.oj.contest.enums.ContestModeEnum;
import cn.edu.sdu.qd.oj.contest.enums.ContestOpennessEnum;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDescriptionDTO;
import cn.edu.sdu.qd.oj.submit.dto.*;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionCreateReqDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListReqDTO;
import cn.edu.sdu.qd.oj.submit.enums.SubmissionJudgeResult;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    /**
    * @Description 查询比赛，同时对用户和比赛开始时间进行校验
    **/
    public ContestDTO queryAndValidate(Long contestId, long userId) {
        ContestDO contestDO = contestDao.getById(contestId);
        AssertUtils.notNull(contestDO, ApiExceptionEnum.CONTEST_NOT_FOUND);

        // 取出 feature 来使用，同时置空避免多次转换
        ContestFeatureDTO featureDTO = ContestConvertUtils.featuresTo(contestDO.getFeatures());
        contestDO.setFeatures(null);
        // 鉴权
        if (!contestDO.containsUserIdInParticipants(userId) && ContestOpennessEnum.PRIVATE.equals(featureDTO.getOpenness())) {
            contestDO.setProblems(null);
            contestDO.setMarkdownDescription(null);
        }
        // 比赛未开始无法查题
        if (contestDO.getGmtStart().after(new Date())) {
            contestDO.setProblems(null);
        }

        ContestDTO contestDTO = contestConverter.to(contestDO);
        contestDTO.setFeatures(featureDTO); // 置入 feature
        return contestDTO;
    }

    @Transactional
    @Cacheable(value = ContestCacheTypeManager.CONTEST_OVERVIEW, key = "#contestId+'-'+#userSessionDTO.userId")
    public ContestDTO query(long contestId, UserSessionDTO userSessionDTO) throws InternalApiException {
        ContestDTO contestDTO = queryAndValidate(contestId, userSessionDTO.getUserId());

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

        // 提出 contestFeature
        ContestFeatureDTO contestFeatureDTO = contestDTO.getFeatures();
        ContestFeatureDTO.InfoOpenness infoOpenness = contestDTO.getGmtEnd().after(new Date()) ? contestFeatureDTO.getContestRunning() : contestFeatureDTO.getContestEnd();
        // 查询本人的过题情况
        Map<String, List<SubmissionResultDTO>> problemCodeToSubmissionList = querySubmissionResultList(contestId, contestDTO.getProblems(), userSessionDTO.getUserId())
                .stream().collect(Collectors.groupingBy(SubmissionResultDTO::getProblemCode));
        // problemCode 脱敏成 problemIndex，并置当前用户的 judge 情况
        Optional.ofNullable(contestDTO.getProblems()).ifPresent(problems -> {
            int problemIndex = 0;
            for (ContestProblemListDTO problem : contestDTO.getProblems()) {
                problemIndex++;
                problem.setProblemCode(String.valueOf(problemIndex));
                // 置当前用户对各题的 judge 情况
                for (SubmissionResultDTO submission : Optional.ofNullable(problemCodeToSubmissionList.get(problem.getProblemCode())).orElse(Lists.newArrayList())) {
                    if (infoOpenness.getDisplayJudgeScore() != 0) {
                        problem.setJudgeScore(problem.getJudgeResult() == null ? submission.getJudgeScore() : Math.max(problem.getJudgeScore(), submission.getJudgeScore()));
                    }
                    if (SubmissionJudgeResult.AC.equals(submission.getJudgeResult())) {
                        problem.setJudgeResult(submission.getJudgeResult());
                        break;
                    }
                    problem.setJudgeResult(submission.getJudgeResult());
                }
                // 根据 infoOpenness 将各题的 submitNum acceptNum 脱敏
                if (!(PermissionEnum.SUPERADMIN.in(userSessionDTO) || PermissionEnum.ADMIN.in(userSessionDTO) || userSessionDTO.userIdEquals(contestDTO.getUserId()))) {
                    if (infoOpenness.getDisplayRank() == 0 && infoOpenness.getDisplayPeerSubmission() == 0) {
                        problem.setSubmitNum(problem.getJudgeResult() != null ? 1 : 0);
                        problem.setAcceptNum(SubmissionJudgeResult.AC.equals(problem.getJudgeResult()) ? 1 : 0);
                    }
                }
            }
        });


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

        // 判断比赛可见性
        ContestFeatureDTO featureDTO = ContestConvertUtils.featuresTo(contestDO.getFeatures());
        switch (ContestOpennessEnum.of(featureDTO.getOpenness())) {
            case PUBLIC:
                break;
            case PROTECTED:
            case PRIVATE:
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

    public PageResult<ContestListDTO> page(ContestListReqDTO reqDTO, UserSessionDTO userSessionDTO) {
        LambdaQueryChainWrapper<ContestListDO> query = contestListDao.lambdaQuery()
                .orderByDesc(ContestListDO::getGmtStart);
        if (userSessionDTO != null) {
            if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
                query.and(o1 -> o1.eq(ContestListDO::getIsPublic, 1)
                                  .or(o2 -> o2.eq(ContestListDO::getIsPublic, 0)
                                              .and(o3 -> o3.eq(ContestListDO::getUserId, userSessionDTO.getUserId()))));
            }
        } else {
            query.and(o1 -> o1.eq(ContestListDO::getIsPublic, 1));
        }

        // TODO: 修改掉临时的暴力 feature 匹配
        Optional.ofNullable(reqDTO).map(ContestListReqDTO::getMode).filter(StringUtils::isNotBlank).ifPresent(mode -> {
            query.like(ContestListDO::getFeatures, String.format("mode:\"%s", mode));
        });

        Page<ContestListDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        return new PageResult<>(pageResult.getPages(), contestListConverter.to(pageResult.getRecords()));
    }

    /**
    * @Description 查询一个最近的比赛
    **/
    public ContestListDTO queryUpcomingContest() {
        ContestListDO contestListDO = contestListDao.lambdaQuery()
                .orderByAsc(ContestListDO::getGmtStart)
                .eq(ContestListDO::getIsPublic, 1)
                .ge(ContestListDO::getGmtStart, new Date()).last("limit 1").one();
        return contestListConverter.to(contestListDO);
    }

    public ContestProblemDTO queryProblem(long contestId, int problemIndex, long userId) {
        ContestDO contestDO = queryContestAndValidate(contestId, userId);
        ContestProblemListDTO contestProblemListDTO = contestDO.getProblemCodeByIndex(problemIndex);
        // RPC查询题目
        ProblemDTO problemDTO = problemClient.queryProblemWithDescriptionId(contestProblemListDTO.getProblemCode(), contestProblemListDTO.getProblemDescriptionId());
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
                ContestDO::getContestId,
                ContestDO::getFeatures,
                ContestDO::getGmtStart,
                ContestDO::getGmtEnd,
                ContestDO::getUserId,
                ContestDO::getProblems,
                ContestDO::getParticipants
        ).eq(ContestDO::getContestId, contestId).one();
        AssertUtils.notNull(contestDO, ApiExceptionEnum.CONTEST_NOT_FOUND);

        // 判断比赛可见性
        ContestFeatureDTO featureDTO = ContestConvertUtils.featuresTo(contestDO.getFeatures());
        ContestOpennessEnum openness = ContestOpennessEnum.of(featureDTO.getOpenness());
        AssertUtils.isTrue(contestDO.containsUserIdInParticipants(userId) || !ContestOpennessEnum.PRIVATE.equals(openness), ApiExceptionEnum.CONTEST_NOT_PARTICIPATE);

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
        AssertUtils.isTrue(contestDO.getGmtStart().before(new Date()), ApiExceptionEnum.CONTEST_NOT_BEGIN);

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

    public PageResult<ContestSubmissionListDTO> listSubmission(ContestSubmissionListReqDTO reqDTO, UserSessionDTO userSessionDTO) {
        ContestDO contestDO = queryContestAndValidate(reqDTO.getContestId(), userSessionDTO.getUserId());

        // 脱敏, 但管理员/出题者能查所有的提交
        ContestFeatureDTO contestFeatureDTO = ContestConvertUtils.featuresTo(contestDO.getFeatures());
        ContestFeatureDTO.InfoOpenness infoOpenness = contestDO.getGmtEnd().after(new Date()) ? contestFeatureDTO.getContestRunning() : contestFeatureDTO.getContestEnd();
        if (infoOpenness.getDisplayPeerSubmission() == 0 &&
           !(PermissionEnum.SUPERADMIN.in(userSessionDTO) || PermissionEnum.ADMIN.in(userSessionDTO) || userSessionDTO.userIdEquals(contestDO.getUserId()))) {
            reqDTO.setUsername(userClient.userIdToUsername(userSessionDTO.getUserId()));
        }


        // 构造 problemCode To ProblemIndex Map
        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        List<String> problemCodeList = contestProblemListDTOList.stream().map(ContestProblemListDTO::getProblemCode).collect(Collectors.toList());
        Map<String, Integer> problemCodeToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemCodeToProblemIndexMap.put(contestProblemListDTOList.get(i).getProblemCode(), i + 1);
        }

        // 指定了 problemIndex 的查询
        String problemCode = Optional.ofNullable(reqDTO.getProblemIndex())
                .map(contestDO::getProblemCodeByIndex)
                .map(ContestProblemListDTO::getProblemCode)
                .orElse(null);

        // 构造查询
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

            // 数据脱敏
            if (infoOpenness.getDisplayJudgeScore() == 0) {
                contestSubmissionListDTOList.forEach(c -> c.setJudgeScore(null));
            }

            return new PageResult<>(pageResult.getTotalPage(), contestSubmissionListDTOList);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.INTERNAL_ERROR, e.getMessage());
        }
    }

//    public List<String> queryACProblem(Long userId, long contestId) {
//        ContestDO contestDO = queryContestAndValidate(contestId, userId);
//        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
//        Map<String, String> problemCodeToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
//        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
//            problemCodeToProblemIndexMap.put(contestProblemListDTOList.get(i).getProblemCode(), String.valueOf(i + 1));
//        }
//        List<String> problemCodeList = Optional.ofNullable(submissionClient.queryACProblem(userId, contestId)).orElse(Lists.newArrayList());
//        return problemCodeList.stream().map(problemCodeToProblemIndexMap::get).filter(Objects::nonNull).collect(Collectors.toList());
//    }

    public SubmissionDTO querySubmission(Long submissionId, long contestId, UserSessionDTO userSessionDTO) throws InternalApiException {
        SubmissionDTO submissionDTO = submissionClient.query(submissionId, contestId);
        if (submissionDTO == null) {
            return null;
        }

        ContestDO contestDO = queryContestAndValidate(contestId, userSessionDTO.getUserId());

        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        Map<Long, Integer> problemIdToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemIdToProblemIndexMap.put(problemClient.problemCodeToProblemId(contestProblemListDTOList.get(i).getProblemCode()), i + 1);
        }

        // problemId、problemCode 脱敏
        submissionDTO.setProblemCode(problemIdToProblemIndexMap.get(submissionDTO.getProblemId()).toString());
        submissionDTO.setProblemId(null);

        // 超级管理员/出题者 直接看所有代码
        if (PermissionEnum.SUPERADMIN.in(userSessionDTO) || userSessionDTO.userIdEquals(contestDO.getUserId())) {
            return submissionDTO;
        }

        // 观看他人代码脱敏
        if (!submissionDTO.getUserId().equals(userSessionDTO.getUserId())) {
            submissionDTO.setCode(null);
            submissionDTO.setCheckpointResults(null);
            submissionDTO.setCheckpointNum(null);
        }
        // 比赛配置脱敏数据
        ContestFeatureDTO contestFeatureDTO = ContestConvertUtils.featuresTo(contestDO.getFeatures());
        ContestFeatureDTO.InfoOpenness infoOpenness = contestDO.getGmtEnd().after(new Date()) ? contestFeatureDTO.getContestRunning() : contestFeatureDTO.getContestEnd();
        if (infoOpenness.getDisplayJudgeScore() == 0) {
            submissionDTO.setJudgeScore(null);
        }
        if (infoOpenness.getDisplayCheckpointResult() == 0) {
            submissionDTO.setCheckpointResults(null);
            submissionDTO.setCheckpointNum(null);
        }

        return submissionDTO;
    }

    /**
    * @Description 查询榜单数据，并做越权、脱敏、榜单冻结
    **/
    @Cacheable(value = ContestCacheTypeManager.RANK, key = "#contestId+'-'+#userSessionDTO.userId")
    public List<ContestRankDTO> queryRank(long contestId, UserSessionDTO userSessionDTO) throws InternalApiException {
        // 查比赛
        ContestDO contestDO = queryContestAndValidate(contestId, userSessionDTO.getUserId());
        // 提出 contestFeature
        ContestFeatureDTO contestFeatureDTO = ContestConvertUtils.featuresTo(contestDO.getFeatures());
        ContestFeatureDTO.InfoOpenness infoOpenness = contestDO.getGmtEnd().after(new Date()) ? contestFeatureDTO.getContestRunning() : contestFeatureDTO.getContestEnd();
        ContestModeEnum contestMode = ContestModeEnum.of(contestFeatureDTO.getMode());
        // 查询 raw 的榜单数据
        List<ContestRankDTO> contestRankDTOList = queryRawRankData(contestDO);

        // 超级管理员/出题者 直接获取所有榜单，无封榜无脱敏
        if (PermissionEnum.SUPERADMIN.in(userSessionDTO) || userSessionDTO.userIdEquals(contestDO.getUserId())) {
            return contestRankDTOList;
        }

        // 禁止显示榜单，脱敏其他人的提交信息
        if (infoOpenness.getDisplayRank() == 0) {
            contestRankDTOList = contestRankDTOList.stream().filter(o -> userSessionDTO.userIdEquals(o.getUserId())).collect(Collectors.toList());
        }
        // 禁止显示分数
        if (infoOpenness.getDisplayJudgeScore() == 0) {
            contestRankDTOList.stream().map(ContestRankDTO::getSubmissions).forEach(o -> o.forEach(s -> s.setJudgeScore(0)));
        }

        // TODO: 挂星选手

        // 榜单冻结
        if (contestFeatureDTO.getFrozenTime() != 0) {
            Date frozenTime = new Date(contestDO.getGmtEnd().getTime() - contestFeatureDTO.getFrozenTime() * 60000);
            contestRankDTOList.forEach(o -> {
                o.frozenRank(frozenTime);
                o.toComputeProblemResults(contestMode);
            });
        }
        // 比赛进行时，需要进行 submissions 转 problemResults，将提交中的蕴涵的信息脱掉
        if (contestDO.getGmtEnd().after(new Date())) {
            contestRankDTOList.forEach(o -> o.toComputeProblemResults(contestMode));
        }

        return contestRankDTOList;
    }

    /**
    * @Description 查询原生的榜单数据，即所有的提交
    **/
    @Cacheable(value = ContestCacheTypeManager.RAW_RANK, key = "#contestDO.contestId")
    public List<ContestRankDTO> queryRawRankData(ContestDO contestDO) throws InternalApiException {
        // raw problem 数据转换
        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(contestDO.getProblems());
        // 查询该比赛的所有提交结果
        List<SubmissionResultDTO> submissionResultDTOList = querySubmissionResultList(contestDO.getContestId(), contestProblemListDTOList, null);
        // 构造每个参赛者的提交列表
        Map<Long, List<SubmissionResultDTO>> userIdToSubmissionListMap = submissionResultDTOList.stream().collect(Collectors.groupingBy(SubmissionResultDTO::getUserId));
        // 转换成 contestRankDTO
        List<ContestRankDTO> contestRankDTOList = ContestRankDTO.create(userIdToSubmissionListMap, contestProblemListDTOList.size());
        // 置入 username 数据
        contestRankDTOList.forEach(o -> o.setUsername(userClient.userIdToUsername(o.getUserId())));
        return contestRankDTOList;
    }

    /**
    * @Description 批量查询提交结果列表
    **/
    @Cacheable(value = ContestCacheTypeManager.SUBMISSION_RESULT_LIST, key = "#contestId", unless="#userId == null")
    public List<SubmissionResultDTO> querySubmissionResultList(long contestId,
                                                               List<ContestProblemListDTO> contestProblemListDTOList,
                                                               Long userId) throws InternalApiException {
        // 查提交
        List<SubmissionResultDTO> submissionResultDTOList = submissionClient.listResult(contestId, userId);
        // 构造 problemId 到 problemIndex 的映射
        Map<Long, Integer> problemIdToProblemIndexMap = new HashMap<>(contestProblemListDTOList.size());
        for (int i = 0, n = contestProblemListDTOList.size(); i < n; i++) {
            problemIdToProblemIndexMap.put(problemClient.problemCodeToProblemId(contestProblemListDTOList.get(i).getProblemCode()), i + 1);
        }
        // problemCode 脱敏
        submissionResultDTOList = submissionResultDTOList.stream()
                .peek(o -> o.setProblemCode(Optional.ofNullable(o.getProblemId())
                        .map(problemIdToProblemIndexMap::get)
                        .map(Long::toString)
                        .orElse(null)))
                .filter(o -> Objects.nonNull(o.getProblemCode()))
                .collect(Collectors.toList());
        return submissionResultDTOList;
    }

    public void invalidateSubmission(long contestId, long submissionId, UserSessionDTO userSessionDTO) {
        // 查比赛
        ContestDO contestDO = queryContestAndValidate(contestId, userSessionDTO.getUserId());
        // 超级管理员/出题者才可
        AssertUtils.isTrue(PermissionEnum.SUPERADMIN.in(userSessionDTO) || userSessionDTO.userIdEquals(contestDO.getUserId()), ApiExceptionEnum.USER_NOT_MATCHING);
        // 取消成绩
        AssertUtils.isTrue(this.submissionClient.invalidateSubmission(submissionId, contestId), ApiExceptionEnum.SERVER_BUSY);
    }
}