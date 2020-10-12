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

import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.util.*;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private ProblemDao problemDao;

    @Autowired
    private ProblemManageListDao problemManageListDao;

    @Autowired
    private ProblemDescriptionDao problemDescriptionDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private ProblemManageConverter problemManageConverter;

    @Autowired
    private ProblemManageListConverter problemManageListConverter;

    @Autowired
    private ProblemDescriptionConverter problemDescriptionConverter;

    @Autowired
    private ProblemDescriptionListConverter problemDescriptionListConverter;

    @Autowired
    private ProblemCacheUtils problemCacheUtils;

    public ProblemManageDTO queryByCode(String problemCode) {
        ProblemDO problemManageDO = problemDao.lambdaQuery().eq(ProblemDO::getProblemCode, problemCode).one();
        AssertUtils.notNull(problemManageDO, ApiExceptionEnum.PROBLEM_NOT_FOUND);
        ProblemManageDTO problemManageDTO = problemManageConverter.to(problemManageDO);
        problemManageDTO.setUsername(userCacheUtils.getUsername(problemManageDO.getUserId()));
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
        // 更新缓存
        redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE,
                String.valueOf(problem.getProblemId()),
                problem.getProblemTitle());
        return problemDO.getProblemCode();
    }

    public PageResult<ProblemManageListDTO> queryProblemByPage(ProblemListReqDTO reqDTO,
                                                               UserSessionDTO userSessionDTO) {
        // 构造 query，只查 public 题或自己的题
        LambdaQueryChainWrapper<ProblemManageListDO> query = problemManageListDao.lambdaQuery()
            .and(o1 -> o1.eq(ProblemManageListDO::getIsPublic, 1)
                         .or(o2 -> o2.eq(ProblemManageListDO::getIsPublic, 0)
                                     .and(o3 -> o3.eq(ProblemManageListDO::getUserId, userSessionDTO.getUserId()))));
        // 置排序条件
        Optional.ofNullable(reqDTO.getOrderBy()).filter(StringUtils::isNotBlank).ifPresent(orderBy -> {
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
        problemManageListDTOlist.forEach(problemManageListDTO -> problemManageListDTO.setUsername(userCacheUtils.getUsername(problemManageListDTO.getUserId())));
        return new PageResult<>(pageResult.getPages(), problemManageListDTOlist);
    }

    public void update(ProblemManageDTO problem) {
        ProblemDO problemDO = problemManageConverter.from(problem);
        log.info("{} -> {}", problem, problemDO);
        AssertUtils.isTrue(problemDao.lambdaUpdate().eq(ProblemDO::getProblemCode, problemDO.getProblemCode()).update(problemDO), ApiExceptionEnum.UNKNOWN_ERROR);
        if (problemDO.getProblemTitle() != null) {
            redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE,
                    String.valueOf(problem.getProblemId()),
                    problem.getProblemTitle());
        }
        if (problemDO.getCheckpointNum() != null) {
            redisUtils.hset(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM,
                    String.valueOf(problemDO.getProblemId()), problemDO.getCheckpointNum());
        }
    }

    public long createDescription(ProblemDescriptionDTO problemDescriptionDTO) {
        problemDescriptionDTO.setProblemId(problemCacheUtils.getProblemId(problemDescriptionDTO.getProblemCode()));

        ProblemDescriptionDO problemDescriptionDO = problemDescriptionConverter.from(problemDescriptionDTO);
        AssertUtils.isTrue(problemDescriptionDao.save(problemDescriptionDO), ApiExceptionEnum.UNKNOWN_ERROR);
        return problemDescriptionDO.getId();
    }

    public void updateDescription(ProblemDescriptionDTO problemDescriptionDTO) {
        ProblemDescriptionDO problemDescriptionDO = problemDescriptionConverter.from(problemDescriptionDTO);
        problemDescriptionDao.lambdaUpdate()
                .eq(ProblemDescriptionDO::getId, problemDescriptionDO.getId())
                .eq(ProblemDescriptionDO::getUserId, problemDescriptionDO.getUserId())
                .update(problemDescriptionDO);
    }

    public ProblemDescriptionDTO queryDescription(long id, Long userId) {
        // TODO: 超级管理员能看到所有
        LambdaQueryChainWrapper<ProblemDescriptionDO> query = problemDescriptionDao.lambdaQuery()
            .and(o1 -> o1.eq(ProblemDescriptionDO::getIsPublic, 1)
                         .or(o2 -> o2.eq(ProblemDescriptionDO::getIsPublic, 0)
                                     .and(o3 -> o3.eq(ProblemDescriptionDO::getUserId, userId))));
        ProblemDescriptionDO problemDescriptionDO = query.eq(ProblemDescriptionDO::getId, id).one();
        return problemDescriptionConverter.to(problemDescriptionDO);
    }

    public List<ProblemDescriptionListDTO> queryDescriptionList(String problemCode, Long userId) {
        long problemId = problemCacheUtils.getProblemId(problemCode);
        // TODO: 超级管理员能看到所有
        LambdaQueryChainWrapper<ProblemDescriptionDO> query = problemDescriptionDao.lambdaQuery().select(
                ProblemDescriptionDO::getId,
                ProblemDescriptionDO::getProblemId,
                ProblemDescriptionDO::getVoteNum,
                ProblemDescriptionDO::getUserId,
                ProblemDescriptionDO::getTitle
        ).and(o1 -> o1.eq(ProblemDescriptionDO::getIsPublic, 1)
                      .or(o2 -> o2.eq(ProblemDescriptionDO::getIsPublic, 0)
                                  .and(o3 -> o3.eq(ProblemDescriptionDO::getUserId, userId))));
        List<ProblemDescriptionDO> problemDescriptionDOList = query.eq(ProblemDescriptionDO::getProblemId, problemId).list();
        List<ProblemDescriptionListDTO> problemDescriptionDTOList = problemDescriptionListConverter.to(problemDescriptionDOList);
        problemDescriptionDTOList.forEach(o -> {
            o.setProblemCode(problemCode);
            o.setUsername(userCacheUtils.getUsername(o.getUserId()));
        });
        return problemDescriptionDTOList;
    }
}