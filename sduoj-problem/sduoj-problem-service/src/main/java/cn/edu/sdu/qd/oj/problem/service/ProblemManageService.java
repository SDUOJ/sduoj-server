/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointDTO;
import cn.edu.sdu.qd.oj.checkpoint.service.CheckpointManageService;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.util.*;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateListDTO;
import cn.edu.sdu.qd.oj.judgetemplate.service.JudgeTemplateService;
import cn.edu.sdu.qd.oj.problem.client.UserClient;
import cn.edu.sdu.qd.oj.problem.converter.*;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDescriptionDao;
import cn.edu.sdu.qd.oj.problem.dao.ProblemManageListDao;
import cn.edu.sdu.qd.oj.problem.dto.*;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemManageListDO;
import cn.edu.sdu.qd.oj.tag.dto.TagDTO;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName ProblemManageService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:31
 * @Version V1.0
 **/

@Service
@Slf4j
public class ProblemManageService {

    @Autowired
    private ProblemCommonService problemCommonService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private CheckpointManageService checkpointManageService;

    @Autowired
    private ProblemExtensionSerivce problemExtensionSerivce;

    @Autowired
    private JudgeTemplateService judgeTemplateService;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private ProblemManageListDao problemManageListDao;

    @Autowired
    private ProblemDescriptionDao problemDescriptionDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ProblemManageConverter problemManageConverter;

    @Autowired
    private ProblemManageListConverter problemManageListConverter;

    @Autowired
    private ProblemDescriptionConverter problemDescriptionConverter;

    @Autowired
    private ProblemDescriptionListConverter problemDescriptionListConverter;

    @Autowired
    private UserClient userClient;

    public ProblemManageDTO queryByCode(String problemCode) {
        ProblemDO problemManageDO = problemDao.lambdaQuery().eq(ProblemDO::getProblemCode, problemCode).one();
        AssertUtils.notNull(problemManageDO, ApiExceptionEnum.PROBLEM_NOT_FOUND);
        ProblemManageDTO problemManageDTO = problemManageConverter.to(problemManageDO);
        problemManageDTO.setUsername(userClient.userIdToUsername(problemManageDO.getUserId()));
        return problemManageDTO;
    }

    @Transactional
    public String createProblem(ProblemManageDTO problem) {
        problem.setProblemId(null);
        problem.setProblemCode(null);
        ProblemDO problemDO = problemManageConverter.from(problem);
        AssertUtils.isTrue(problemDao.save(problemDO), ApiExceptionEnum.UNKNOWN_ERROR);
        // TODO: 魔法值解决
        problemDO.setProblemCode("SDUOJ-" + problemDO.getProblemId());
        if (!problemDao.lambdaUpdate()
                .eq(ProblemDO::getProblemId, problemDO.getProblemId())
                .set(ProblemDO::getProblemCode, problemDO.getProblemCode())
                .update()) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        return problemDO.getProblemCode();
    }

    public PageResult<ProblemManageListDTO> queryProblemByPage(ProblemListReqDTO reqDTO,
                                                               UserSessionDTO userSessionDTO) {
        LambdaQueryChainWrapper<ProblemManageListDO> query = problemManageListDao.lambdaQuery();
        // 超级管理员能查所有的题，其他只查 public 题或自己的题
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            Long userId = Optional.ofNullable(userSessionDTO).map(UserSessionDTO::getUserId).orElse(null);
            query.and(o1 -> o1.eq(ProblemManageListDO::getIsPublic, 1)
                              .or(o2 -> o2.eq(ProblemManageListDO::getIsPublic, 0)
                                          .and(o3 -> o3.eq(ProblemManageListDO::getUserId, userId))));
        }
        // 置排序条件
        Optional.ofNullable(reqDTO.getSortBy()).filter(StringUtils::isNotBlank).ifPresent(orderBy -> {
            switch (orderBy) {
                case "acceptNum":
                    query.orderBy(true, reqDTO.getAscending(), ProblemManageListDO::getAcceptNum);
                    break;
                default:
                    break;
            }
        });
        // 置等值条件
        Optional.ofNullable(reqDTO.getRemoteOj()).filter(StringUtils::isNotBlank).ifPresent(remoteOj -> {
            query.eq(ProblemManageListDO::getRemoteOj, remoteOj);
        });
        // 分页查结果
        Page<ProblemManageListDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        if (pageResult.getSize() == 0) {
            return new PageResult<>();
        }
        // 转换
        List<ProblemManageListDTO> problemManageListDTOlist = problemManageListConverter.to(pageResult.getRecords());
        // 查询 tagDTOMap
        Map<Long, TagDTO> tagIdToDTOMap = problemCommonService.getTagDTOMapByProblemListDOList(pageResult.getRecords());
        // 置入 tagDTOList
        problemManageListDTOlist.forEach(o -> o.setTagDTOList(
                problemCommonService.getTagIdListByFeatureMap(o.getFeatures()).stream().map(tagIdToDTOMap::get).collect(Collectors.toList())
        ));
        // 置入 username
        problemManageListDTOlist.forEach(problemManageListDTO -> problemManageListDTO.setUsername(userClient.userIdToUsername(problemManageListDTO.getUserId())));
        // 查询 judgeTemplate，置入
        List<Long> judgeTemplateIdList = problemManageListDTOlist.stream()
                .map(ProblemManageListDTO::getJudgeTemplates)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        List<JudgeTemplateListDTO> judgeTemplateListDTOList = judgeTemplateService.listByIds(judgeTemplateIdList);
        Map<Long, JudgeTemplateListDTO> judgeTemplateListDTOMap = judgeTemplateListDTOList.stream().collect(Collectors.toMap(JudgeTemplateListDTO::getId, Function.identity(), (k1, k2) -> k1));
        problemManageListDTOlist.forEach(problemManageListDTO -> {
            Optional.ofNullable(problemManageListDTO.getJudgeTemplates()).filter(CollectionUtils::isNotEmpty).ifPresent(ids ->
                problemManageListDTO.setJudgeTemplateListDTOList(ids.stream().map(judgeTemplateListDTOMap::get).collect(Collectors.toList()))
            );
        });
        // 包装, 返回
        return new PageResult<>(pageResult.getPages(), problemManageListDTOlist);
    }

    @Transactional
    public void update(ProblemManageDTO problem, UserSessionDTO userSessionDTO) {
        // 查出该题
        ProblemDO originalProblemDO = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getVersion,
                ProblemDO::getUserId
        ).eq(ProblemDO::getProblemCode, problem.getProblemCode()).one();
        // 特判题目权限
        AssertUtils.notNull(originalProblemDO, ApiExceptionEnum.PROBLEM_NOT_FOUND);
        AssertUtils.isTrue(PermissionEnum.SUPERADMIN.in(userSessionDTO) || userSessionDTO.userIdEquals(originalProblemDO.getUserId()),
                ApiExceptionEnum.USER_NOT_MATCHING);
        problem.setProblemId(originalProblemDO.getProblemId());

        // 构造更新器
        ProblemDO problemUpdateDO = problemManageConverter.from(problem);
        problemUpdateDO.setVersion(originalProblemDO.getVersion());

        log.info("{} -> {}", problem, problemUpdateDO);

        // 更新题目
        AssertUtils.isTrue(problemDao.updateById(problemUpdateDO), ApiExceptionEnum.UNKNOWN_ERROR);
        // 如果传的 caseList 不为 null，则更新 checkpointCase (增量更新和全量更新区别开)
        if (problem.getCheckpointCases() != null) {
            if (problemUpdateDO.getCheckpointCases() == null) {
                problemDao.lambdaUpdate()
                        .set(ProblemDO::getCheckpointCases, null)
                        .eq(ProblemDO::getProblemId, originalProblemDO.getProblemId())
                        .update();
            }
            updateCheckpointCase(problem);
        }
    }

    private void updateCheckpointCase(ProblemManageDTO problem) {
        List<Long> checkpointCases = problem.getCheckpointCases();
        if (CollectionUtils.isEmpty(checkpointCases)) {
            problemExtensionSerivce.updateProblemCase(problem.getProblemId(), Lists.newArrayList());
            return;
        }
        List<CheckpointDTO> checkpointDTOList = checkpointManageService.listByIdList(checkpointCases);
        List<ProblemCaseDTO> problemCaseDTOList = checkpointDTOList.stream()
                .map(o -> ProblemCaseDTO.builder().input(o.getInputPreview()).output(o.getOutputPreview()).build())
                .collect(Collectors.toList());
        problemExtensionSerivce.updateProblemCase(problem.getProblemId(), problemCaseDTOList);
    }

    public long createDescription(ProblemDescriptionDTO problemDescriptionDTO) {
        problemDescriptionDTO.setProblemId(problemService.problemCodeToProblemId(problemDescriptionDTO.getProblemCode()));

        ProblemDescriptionDO problemDescriptionDO = problemDescriptionConverter.from(problemDescriptionDTO);
        AssertUtils.isTrue(problemDescriptionDao.save(problemDescriptionDO), ApiExceptionEnum.UNKNOWN_ERROR);
        return problemDescriptionDO.getId();
    }

    public void updateDescription(ProblemDescriptionDTO problemDescriptionDTO) {
        ProblemDescriptionDO problemDescriptionDO = problemDescriptionConverter.from(problemDescriptionDTO);
        LambdaUpdateChainWrapper<ProblemDescriptionDO> updater = problemDescriptionDao.lambdaUpdate()
                .eq(ProblemDescriptionDO::getId, problemDescriptionDO.getId());
        Optional.of(problemDescriptionDO).map(ProblemDescriptionDO::getUserId).ifPresent(userId -> {
            updater.eq(ProblemDescriptionDO::getUserId, userId);
        });
        AssertUtils.isTrue(updater.update(problemDescriptionDO), ApiExceptionEnum.UNKNOWN_ERROR, "或 修改他人题面出错");
    }

    public ProblemDescriptionDTO queryDescription(long id, UserSessionDTO userSessionDTO) {
        LambdaQueryChainWrapper<ProblemDescriptionDO> query = problemDescriptionDao.lambdaQuery();
        // 超级管理员能看到所有
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            Long userId = Optional.ofNullable(userSessionDTO).map(UserSessionDTO::getUserId).orElse(null);
            query.and(o1 -> o1.eq(ProblemDescriptionDO::getIsPublic, 1)
                              .or(o2 -> o2.eq(ProblemDescriptionDO::getIsPublic, 0)
                                          .and(o3 -> o3.eq(ProblemDescriptionDO::getUserId, userId))));
        }
        ProblemDescriptionDO problemDescriptionDO = query.eq(ProblemDescriptionDO::getId, id).one();
        return problemDescriptionConverter.to(problemDescriptionDO);
    }

    public List<ProblemDescriptionListDTO> queryDescriptionList(String problemCode, UserSessionDTO userSessionDTO) {
        long problemId = problemService.problemCodeToProblemId(problemCode);
        ProblemDO problemDO = problemDao.lambdaQuery().select(ProblemDO::getDefaultDescriptionId).eq(ProblemDO::getProblemId, problemId).one();
        LambdaQueryChainWrapper<ProblemDescriptionDO> query = problemDescriptionDao.lambdaQuery().select(
                ProblemDescriptionDO::getId,
                ProblemDescriptionDO::getIsPublic,
                ProblemDescriptionDO::getProblemId,
                ProblemDescriptionDO::getVoteNum,
                ProblemDescriptionDO::getUserId,
                ProblemDescriptionDO::getTitle
        );
        // superadmin 能看到所有，admin 只能看到公开/默认/自己的题面
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            Long userId = Optional.ofNullable(userSessionDTO).map(UserSessionDTO::getUserId).orElse(null);
            query.and(o1 -> o1.eq(ProblemDescriptionDO::getIsPublic, 1)
                              .or(o2 -> o2.eq(ProblemDescriptionDO::getIsPublic, 0)
                                          .and(o3 -> o3.eq(ProblemDescriptionDO::getUserId, userId)))
                              .or(o4 -> o4.eq(ProblemDescriptionDO::getId, problemDO.getDefaultDescriptionId())));
        }
        List<ProblemDescriptionDO> problemDescriptionDOList = query.eq(ProblemDescriptionDO::getProblemId, problemId).list();
        List<ProblemDescriptionListDTO> problemDescriptionDTOList = problemDescriptionListConverter.to(problemDescriptionDOList);
        problemDescriptionDTOList.forEach(o -> {
            o.setProblemCode(problemCode);
            o.setUsername(userClient.userIdToUsername(o.getUserId()));
        });
        return problemDescriptionDTOList;
    }
}