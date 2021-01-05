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
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.problem.client.UserClient;
import cn.edu.sdu.qd.oj.problem.converter.*;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDescriptionDao;
import cn.edu.sdu.qd.oj.problem.dto.ProblemCaseDTO;
import cn.edu.sdu.qd.oj.tag.converter.TagConverter;
import cn.edu.sdu.qd.oj.tag.dao.TagDao;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListReqDTO;
import cn.edu.sdu.qd.oj.tag.dto.TagDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;

import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDOField;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;
import cn.edu.sdu.qd.oj.tag.entity.TagDO;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProblemService {

    @Autowired
    private ProblemCommonService problemCommonService;

    @Autowired
    private ProblemExtensionSerivce problemExtensionSerivce;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private ProblemDescriptionDao problemDescriptionDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ProblemConverter problemConverter;

    @Autowired
    private ProblemListConverter problemListConverter;

    @Autowired
    private TagConverter tagConverter;

    @Autowired
    private UserClient userClient;

    public ProblemDTO queryByCode(String problemCode, Long descriptionId, UserSessionDTO userSessionDTO) {
        // TODO: cache, polish

        Long userId = Optional.ofNullable(userSessionDTO).map(UserSessionDTO::getUserId).orElse(null);

        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO.class, field -> !field.getColumn().equals(ProblemDOField.CHECKPOINTS)
        ).eq(ProblemDO::getProblemCode, problemCode).one();
        AssertUtils.notNull(problemDO, ApiExceptionEnum.PROBLEM_NOT_FOUND);

        // 非公开题只能出题者和超管查询
        AssertUtils.isTrue(problemDO.getIsPublic() == 1 || problemDO.getUserId().equals(userId) || PermissionEnum.SUPERADMIN.in(userSessionDTO), ApiExceptionEnum.USER_NOT_MATCHING);

        // 查询题目描述
        ProblemDescriptionDO problemDescriptionDO = problemDescriptionDao.lambdaQuery()
                .eq(ProblemDescriptionDO::getProblemId, problemDO.getProblemId())
                .eq(ProblemDescriptionDO::getId, descriptionId != null ? descriptionId : problemDO.getDefaultDescriptionId())
                .one();
        // 若非 superadmin 则进行过滤，过滤掉非公开且非默认且非自己的题面
        if (problemDescriptionDO != null && problemDescriptionDO.getIsPublic() == 0 &&
                !problemDescriptionDO.getId().equals(problemDO.getDefaultDescriptionId()) &&
                !problemDescriptionDO.getUserId().equals(userId) &&
                 PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            problemDescriptionDO = null;
        }

        // 查询所有公开的题面 list
        LambdaQueryChainWrapper<ProblemDescriptionDO> descriptionListQuery = problemDescriptionDao.lambdaQuery().select(
                ProblemDescriptionDO::getIsPublic,
                ProblemDescriptionDO::getId,
                ProblemDescriptionDO::getProblemId,
                ProblemDescriptionDO::getUserId,
                ProblemDescriptionDO::getVoteNum,
                ProblemDescriptionDO::getTitle
        ).eq(ProblemDescriptionDO::getProblemId, problemDO.getProblemId());
        // 查询，若非 superadmin 则进行过滤，过滤掉非公开非自己非默认的题面，并按照 descriptionId 排序
        List<ProblemDescriptionDO> problemDescriptionDOList = descriptionListQuery.list();
        problemDescriptionDOList.sort(ProblemDescriptionDO::compareById);
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            problemDescriptionDOList = problemDescriptionDOList.stream()
                .filter(o -> o.getIsPublic() == 1 || (o.getIsPublic() == 0 && o.getUserId().equals(userId)) || o.getId().equals(problemDO.getDefaultDescriptionId()))
                .collect(Collectors.toList());
        }

        // 查询 problemCase
        List<ProblemCaseDTO> problemCaseDTOList = problemExtensionSerivce.queryProblemCase(problemDO.getProblemId());

        ProblemDTO problemDTO = problemConverter.to(problemDO, problemDescriptionDO, problemDescriptionDOList, problemCaseDTOList);

        // TODO: 考虑设计一个 annotation 和 cacheUtil 关联起来，自动填充一些业务字段
        try {
            problemDTO.getProblemDescriptionDTO().setProblemCode(problemCode);
            problemDTO.getProblemDescriptionListDTOList().forEach(o -> {
                o.setProblemCode(problemCode);
                o.setUsername(userClient.userIdToUsername(o.getUserId()));
            });
        } catch (Exception ignore) {
        }

        // 置 tagDTO
        List<Long> tags = getTagIdListFromFeatureMap(problemDTO.getFeatures());
        if (!CollectionUtils.isEmpty(tags)) {
            List<TagDO> tagDOList = tagDao.lambdaQuery().in(TagDO::getId, tags).list();
            problemDTO.setTagDTOList(tagConverter.to(tagDOList));
        }
        return problemDTO;
    }

    public PageResult<ProblemListDTO> queryProblemByPage(ProblemListReqDTO reqDTO, UserSessionDTO userSessionDTO) {
        LambdaQueryChainWrapper<ProblemDO> query = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getIsPublic,
                ProblemDO::getFeatures,
                ProblemDO::getProblemCode,
                ProblemDO::getProblemTitle,
                ProblemDO::getSource,
                ProblemDO::getRemoteOj,
                ProblemDO::getRemoteUrl,
                ProblemDO::getSubmitNum,
                ProblemDO::getAcceptNum
        );
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            if (userSessionDTO != null) {
                query.and(o1 -> o1.eq(ProblemDO::getIsPublic, 1)
                                  .or(o2 -> o2.eq(ProblemDO::getIsPublic, 0)
                                              .and(o3 -> o3.eq(ProblemDO::getUserId, userSessionDTO.getUserId()))));
            } else {
                query.eq(ProblemDO::getIsPublic, 1);
            }
        }
        Optional.ofNullable(reqDTO.getSortBy()).filter(StringUtils::isNotBlank).ifPresent(sortBy -> {
            switch (sortBy) {
                case "acceptNum":
                    query.orderBy(true, reqDTO.getAscending(), ProblemDO::getAcceptNum);
                    break;
                default:
                    break;
            }
        });
        Optional.ofNullable(reqDTO.getRemoteOj()).filter(StringUtils::isNotBlank).ifPresent(remoteOj -> {
            query.eq(ProblemDO::getRemoteOj, remoteOj);
        });
        // 分页查结果
        Page<ProblemDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        // 转换
        List<ProblemListDTO> problemListDTOList = problemListConverter.to(pageResult.getRecords());
        // 查询 tagDTOMap
        Map<Long, TagDTO> tagIdToDTOMap = problemCommonService.getTagDTOMapByProblemDOList(pageResult.getRecords());
        // 置入 tagDTOList
        problemListDTOList.forEach(o -> o.setTagDTOList(
                problemCommonService.getTagIdListByFeatureMap(o.getFeatures()).stream().map(tagIdToDTOMap::get).collect(Collectors.toList())
        ));
        return new PageResult<>(pageResult.getPages(), Optional.ofNullable(problemListDTOList).orElse(Lists.newArrayList()));
    }

    private List<TagDO> getTagDTOListFromFeatures(String features) {
        if (StringUtils.isBlank(features)) {
            return null;
        }
        Map<String, String> featureMap = Arrays.stream(features.split(";")).collect(Collectors.toMap(s -> s.substring(0, s.indexOf(":")), s -> s.substring(s.indexOf(":") + 1), (k1, k2) -> k1));
        List<Long> tags = getTagIdListFromFeatureMap(featureMap);
        List<TagDO> tagDOList = tagDao.lambdaQuery().in(TagDO::getId, tags).list();
        return tagDOList;
    }

    private List<Long> getTagIdListFromFeatureMap(Map<String, String> featureMap) {
        List<Long> tags = Optional.ofNullable(featureMap)
                .map(map -> map.get("tags"))
                .map(tagsStr -> tagsStr.split(","))
                .map(tagArray -> Arrays.stream(tagArray)
                        .map(Long::parseLong)
                        .collect(Collectors.toList()))
                .orElse(null);
        return tags;
    }

    public Map<Long, String> queryIdToTitleMap() {
        List<ProblemDO> problemDOList = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getProblemTitle
        ).list();
        return problemDOList.stream().collect(Collectors.toMap(ProblemDO::getProblemId, ProblemDO::getProblemTitle, (k1, k2) -> k1));
    }

    public boolean validateProblemCodeList(List<String> problemCodeList) {
        return problemCodeList.size() == problemDao.lambdaQuery().in(ProblemDO::getProblemCode, problemCodeList).count();
    }

    /**
    * @Description 查询题目带题面，不对用户进行鉴权
    **/
    public ProblemDTO queryWithDescriptionId(String problemCode, long problemDescriptionId) {
        ProblemDO problemDO = problemDao.lambdaQuery().eq(ProblemDO::getProblemCode, problemCode).one();
        if (problemDO == null) {
            return null;
        }
        ProblemDescriptionDO problemDescriptionDO = problemDescriptionDao.lambdaQuery()
                .eq(ProblemDescriptionDO::getProblemId, problemDO.getProblemId())
                .eq(ProblemDescriptionDO::getId, problemDescriptionId).one();

        // 查询 problemCase
        List<ProblemCaseDTO> problemCaseDTOList = problemExtensionSerivce.queryProblemCase(problemDO.getProblemId());

        return problemConverter.to(problemDO, problemDescriptionDO, null, problemCaseDTOList);
    }

    public void incProblemAcceptNumber(long problemId) {
        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getVersion,
                ProblemDO::getAcceptNum
        ).eq(ProblemDO::getProblemId, problemId).one();
        problemDO.setAcceptNum(problemDO.getAcceptNum() + 1);
        AssertUtils.isTrue(problemDao.updateById(problemDO), ApiExceptionEnum.SERVER_BUSY);
    }

    public void incProblemSubmitNumber(long problemId) {
        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getVersion,
                ProblemDO::getSubmitNum
        ).eq(ProblemDO::getProblemId, problemId).one();
        problemDO.setSubmitNum(problemDO.getSubmitNum() + 1);
        AssertUtils.isTrue(problemDao.updateById(problemDO), ApiExceptionEnum.SERVER_BUSY);
    }

    public static final long CORRECT_RATE = 60L * 60 * 1000; // 60min
    /**
     * @Description 定时校准题目的 acceptNum 和 submitNum
     **/
    @Scheduled(fixedRate = CORRECT_RATE)
    public void correctAccpetNumAndSubmitNum() {
        log.info("correct acceptNum, submitNum");
        // TODO: 定时校准题目的 acceptNum 和 submitNum

    }


    public List<Long> queryPrivateProblemIdList(Long userId) {
        LambdaQueryChainWrapper<ProblemDO> query = problemDao.lambdaQuery();
        if (userId != null) {
            query.select(ProblemDO::getProblemId)
                    .and(o1 -> o1.eq(ProblemDO::getIsPublic, 0)
                                 .and(o3 -> o3.ne(ProblemDO::getUserId, userId)));
        } else {
            query.select(ProblemDO::getProblemId).eq(ProblemDO::getIsPublic, 0);
        }
        List<ProblemDO> problemDOList = query.list();
        return problemDOList.stream().map(ProblemDO::getProblemId).collect(Collectors.toList());
    }

    public String problemIdToProblemTitle(long problemId) {
        return Optional.ofNullable(problemDao.lambdaQuery()
                .select(ProblemDO::getProblemId, ProblemDO::getProblemTitle)
                .eq(ProblemDO::getProblemId, problemId)
                .one()
        ).map(ProblemDO::getProblemTitle).orElse(null);
    }

    public int problemIdToProblemCheckpointNum(long problemId) {
        return Optional.ofNullable(problemDao.lambdaQuery()
                .select(ProblemDO::getProblemId, ProblemDO::getCheckpointNum)
                .eq(ProblemDO::getProblemId, problemId)
                .one()
        ).map(ProblemDO::getCheckpointNum).orElse(0);
    }

    public Long problemCodeToProblemId(String problemCode) {
        return Optional.ofNullable(problemDao.lambdaQuery()
                .select(ProblemDO::getProblemId)
                .eq(ProblemDO::getProblemCode, problemCode)
                .one()
        ).map(ProblemDO::getProblemId).orElse(null);
    }

    public String problemIdToProblemCode(long problemId) {
        return Optional.ofNullable(problemDao.lambdaQuery()
                .select(ProblemDO::getProblemCode)
                .eq(ProblemDO::getProblemId, problemId)
                .one()
        ).map(ProblemDO::getProblemCode).orElse(null);
    }
}