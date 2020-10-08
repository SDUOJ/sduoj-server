/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.common.util.UserCacheUtils;
import cn.edu.sdu.qd.oj.problem.converter.*;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDescriptionDao;
import cn.edu.sdu.qd.oj.problem.dao.ProblemTagDao;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListReqDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemTagDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;

import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDOField;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemTagDO;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemService {

    @Autowired
    private ProblemCommonService problemCommonService;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private ProblemDescriptionDao problemDescriptionDao;

    @Autowired
    private ProblemTagDao problemTagDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private ProblemConverter problemConverter;

    @Autowired
    private ProblemListConverter problemListConverter;

    @Autowired
    private ProblemTagConverter problemTagConverter;

    public ProblemDTO queryByCode(String problemCode, Long descriptionId, Long userId) {
        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO.class, field -> !field.getColumn().equals(ProblemDOField.CHECKPOINTS)
        ).eq(ProblemDO::getProblemCode, problemCode).one();
        if (problemDO == null) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        if (problemDO.getIsPublic() == 0 && !problemDO.getUserId().equals(userId)) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_PUBLIC);
        }

        // 查询题目描述
        ProblemDescriptionDO problemDescriptionDO = problemDescriptionDao.lambdaQuery()
                .eq(ProblemDescriptionDO::getProblemId, problemDO.getProblemId())
                .eq(ProblemDescriptionDO::getId, descriptionId != null ? descriptionId : problemDO.getDefaultDescriptionId())
                .one();
        // 过滤掉非公开且非默认且非自己的题面
        if (problemDescriptionDO != null &&
            problemDescriptionDO.getIsPublic() == 0 &&
            !problemDescriptionDO.getId().equals(problemDO.getDefaultDescriptionId()) &&
            !problemDescriptionDO.getUserId().equals(userId)) {
            problemDescriptionDO = null;
        }

        // 查询所有公开的题面 list
        LambdaQueryChainWrapper<ProblemDescriptionDO> descriptionListQuery = problemDescriptionDao.lambdaQuery().select(
                ProblemDescriptionDO::getIsPublic,
                ProblemDescriptionDO::getId,
                ProblemDescriptionDO::getProblemId,
                ProblemDescriptionDO::getUserId,
                ProblemDescriptionDO::getVoteNum
        ).eq(ProblemDescriptionDO::getProblemId, problemDO.getProblemId());
        Optional.ofNullable(problemDescriptionDO).map(ProblemDescriptionDO::getId).ifPresent(problemDescriptionId -> {
            descriptionListQuery.ne(ProblemDescriptionDO::getId, problemDescriptionId);
        });
        // 查询并过滤掉非公开非自己的题面
        List<ProblemDescriptionDO> problemDescriptionDOList = descriptionListQuery.list()
                .stream()
                .filter(o -> o.getIsPublic() == 1 || (o.getIsPublic() == 0 && o.getUserId().equals(userId)))
                .collect(Collectors.toList());
        // 将 problemDescriptionDO 加入到 list
        if (problemDescriptionDO != null) {
            problemDescriptionDOList.add(problemDescriptionDO);
        }
        // 按照 descriptionId 排序
        problemDescriptionDOList.sort(ProblemDescriptionDO::compareById);


        ProblemDTO problemDTO = problemConverter.to(problemDO, problemDescriptionDO, problemDescriptionDOList);

        // TODO: 考虑设计一个 annotation 和 cacheUtil 关联起来，自动填充一些业务字段
        try {
            problemDTO.getProblemDescriptionDTO().setProblemCode(problemCode);
            problemDTO.getProblemDescriptionListDTOList().forEach(o -> {
                o.setProblemCode(problemCode);
                o.setUsername(userCacheUtils.getUsername(o.getUserId()));
            });
        } catch (Exception ignore) {
        }

        // 置 tagDTO
        List<Long> tags = getTagIdListFromFeatureMap(problemDTO.getFeatures());
        if (!CollectionUtils.isEmpty(tags)) {
            List<ProblemTagDO> problemTagDOList = problemTagDao.lambdaQuery().in(ProblemTagDO::getId, tags).list();
            problemDTO.setProblemTagDTOList(problemTagConverter.to(problemTagDOList));
        }
        return problemDTO;
    }

    public PageResult<ProblemListDTO> queryProblemByPage(ProblemListReqDTO reqDTO, Long userId) {
        LambdaQueryChainWrapper<ProblemDO> query = problemDao.lambdaQuery();
        query.select(
                ProblemDO::getProblemId,
                ProblemDO::getFeatures,
                ProblemDO::getProblemCode,
                ProblemDO::getProblemTitle,
                ProblemDO::getSource,
                ProblemDO::getRemoteOj,
                ProblemDO::getRemoteUrl,
                ProblemDO::getSubmitNum,
                ProblemDO::getAcceptNum
        ).and(o1 -> o1.eq(ProblemDO::getIsPublic, 1)
                      .or(o2 -> o2.eq(ProblemDO::getIsPublic, 0)
                                  .and(o3 -> o3.eq(ProblemDO::getUserId, userId))));
        Optional.ofNullable(reqDTO.getOrderBy()).filter(StringUtils::isNotBlank).ifPresent(orderBy -> {
            switch (orderBy) {
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
        Map<Long, ProblemTagDTO> tagIdToDTOMap = problemCommonService.getTagDTOMapByProblemDOList(pageResult.getRecords());
        // 置入 tagDTOList
        problemListDTOList.forEach(o -> o.setProblemTagDTOList(
            problemCommonService.getTagIdListByFeatureMap(o.getFeatures()).stream().map(tagIdToDTOMap::get).collect(Collectors.toList())
        ));
        return new PageResult<>(pageResult.getPages(), problemListDTOList);
    }

    private List<ProblemTagDO> getTagDTOListFromFeatures(String features) {
        if (StringUtils.isBlank(features)) {
            return null;
        }
        Map<String, String> featureMap = Arrays.stream(features.split(";")).collect(Collectors.toMap(s -> s.substring(0, s.indexOf(":")), s -> s.substring(s.indexOf(":") + 1), (k1, k2) -> k1));
        List<Long> tags = getTagIdListFromFeatureMap(featureMap);
        List<ProblemTagDO> problemTagDOList = problemTagDao.lambdaQuery().in(ProblemTagDO::getId, tags).list();
        return problemTagDOList;
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

    @PostConstruct
    private void initRedisProblemHash() {
        List<ProblemDO> problemDOList = problemDao.lambdaQuery().select(
                ProblemDO::getProblemCode,
                ProblemDO::getProblemId,
                ProblemDO::getProblemTitle,
                ProblemDO::getCheckpointNum
        ).list();
        Map<String, Object> problemIdToTitle = problemDOList.stream().collect(Collectors.toMap(problemDO -> problemDO.getProblemId().toString(), ProblemDO::getProblemTitle, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE, problemIdToTitle);
        Map<String, Object> problemIdToCheckpointNum = problemDOList.stream().collect(Collectors.toMap(problemDO -> problemDO.getProblemId().toString(), ProblemDO::getCheckpointNum, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM, problemIdToCheckpointNum);
        Map<String, Object> problemCodeToProblemId = problemDOList.stream().collect(Collectors.toMap(ProblemDO::getProblemCode, ProblemDO::getProblemId, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_CODE_TO_PROBLEM_ID, problemCodeToProblemId);
        Map<String, Object> problemIdToProblemCode = problemDOList.stream().collect(Collectors.toMap(problemDO -> problemDO.getProblemId().toString(), ProblemDO::getProblemCode, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_PROBLEM_CODE, problemIdToProblemCode);
    }

    public boolean validateProblemCodeList(List<String> problemCodeList) {
        return problemCodeList.size() == problemDao.lambdaQuery().in(ProblemDO::getProblemCode, problemCodeList).count();
    }

    public ProblemDTO queryWithDescriptionId(String problemCode, long problemDescriptionId, long userId) {
        ProblemDO problemDO = problemDao.lambdaQuery().eq(ProblemDO::getProblemCode, problemCode).one();
        if (problemDO == null) {
            return null;
        }
        if (0 == problemDO.getIsPublic() && userId != problemDO.getUserId()) {
            return null;
        }
        ProblemDescriptionDO problemDescriptionDO = problemDescriptionDao.lambdaQuery()
                .eq(ProblemDescriptionDO::getProblemId, problemDO.getProblemId())
                .eq(ProblemDescriptionDO::getId, problemDescriptionId).one();
        return problemConverter.to(problemDO, problemDescriptionDO, null);
    }
}