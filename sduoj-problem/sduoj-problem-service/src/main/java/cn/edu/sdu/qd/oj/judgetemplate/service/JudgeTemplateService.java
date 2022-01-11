/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.service;

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.CollectionUtils;
import cn.edu.sdu.qd.oj.judgetemplate.converter.JudgeTemplateConverter;
import cn.edu.sdu.qd.oj.judgetemplate.converter.JudgeTemplateListConverter;
import cn.edu.sdu.qd.oj.judgetemplate.converter.JudgeTemplateManageListConverter;
import cn.edu.sdu.qd.oj.judgetemplate.dao.JudgeTemplateDao;
import cn.edu.sdu.qd.oj.judgetemplate.dao.JudgeTemplateManageListDao;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateListDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateManageListDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplatePageReqDTO;
import cn.edu.sdu.qd.oj.judgetemplate.entity.JudgeTemplateDO;
import cn.edu.sdu.qd.oj.judgetemplate.entity.JudgeTemplateManageListDO;
import cn.edu.sdu.qd.oj.judgetemplate.enums.JudgeTemplateTypeEnum;
import cn.edu.sdu.qd.oj.problem.service.ProblemService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.assertj.core.util.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JudgeTemplateService {

    @Autowired
    private JudgeTemplateDao judgeTemplateDao;

    @Autowired
    private JudgeTemplateManageListDao judgeTemplateManageListDao;

    @Autowired
    private JudgeTemplateConverter judgeTemplateConverter;

    @Autowired
    private JudgeTemplateManageListConverter judgeTemplateManageListConverter;

    @Autowired
    private JudgeTemplateListConverter judgeTemplateListConverter;

    @Autowired
    private ProblemService problemService;


    public JudgeTemplateDTO query(Long id) {
        JudgeTemplateDO judgeTemplateDO = judgeTemplateDao.getById(id);
        return judgeTemplateConverter.to(judgeTemplateDO);
    }

    public PageResult<JudgeTemplateManageListDTO> page(JudgeTemplatePageReqDTO reqDTO,
                                                       UserSessionDTO userSessionDTO) {
        LambdaQueryChainWrapper<JudgeTemplateManageListDO> query = judgeTemplateManageListDao.lambdaQuery();
        // 超级管理员能查所有的，其他只查 public 或自己的
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            Long userId = Optional.ofNullable(userSessionDTO).map(UserSessionDTO::getUserId).orElse(null);
            query.and(o1 -> o1.eq(JudgeTemplateManageListDO::getIsPublic, 1)
                              .or(o2 -> o2.eq(JudgeTemplateManageListDO::getIsPublic, 0)
                                          .and(o3 -> o3.eq(JudgeTemplateManageListDO::getUserId, userId))));
        }
        Optional.of(reqDTO).map(JudgeTemplatePageReqDTO::getTitle).filter(StringUtils::isNotEmpty).ifPresent(title -> {
            query.likeRight(JudgeTemplateManageListDO::getTitle, title);
        });
        Page<JudgeTemplateManageListDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        List<JudgeTemplateManageListDTO> judgeTemplateManageListDTOList = judgeTemplateManageListConverter.to(pageResult.getRecords());
        return new PageResult<>(pageResult.getPages(), judgeTemplateManageListDTOList);
    }

    public List<JudgeTemplateListDTO> list(int type, String problemCode, UserSessionDTO userSessionDTO) {
        LambdaQueryChainWrapper<JudgeTemplateDO> query = judgeTemplateDao.lambdaQuery().select(
                JudgeTemplateDO::getId,
                JudgeTemplateDO::getType,
                JudgeTemplateDO::getTitle,
                JudgeTemplateDO::getComment,
                JudgeTemplateDO::getAcceptFileExtensions
        );
        query.eq(JudgeTemplateDO::getType, type);
        // 超级管理员能查所有的，其他只查 public 或自己的
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            Long userId = Optional.ofNullable(userSessionDTO).map(UserSessionDTO::getUserId).orElse(null);
            query.and(o1 -> o1.eq(JudgeTemplateDO::getIsPublic, 1)
                              .or(o2 -> o2.eq(JudgeTemplateDO::getIsPublic, 0)
                                          .and(o3 -> o3.eq(JudgeTemplateDO::getUserId, userId))));
        }
        if (JudgeTemplateTypeEnum.ADVANCED.code == type) {
            Long problemId = problemService.problemCodeToProblemId(problemCode);
            query.eq(JudgeTemplateDO::getProblemId, problemId);
        }
        return judgeTemplateListConverter.to(query.list());
    }

    public Long create(JudgeTemplateDTO judgeTemplateDTO, UserSessionDTO userSessionDTO) {
        Optional.ofNullable(judgeTemplateDTO.getProblemCode())
                .map(problemService::problemCodeToProblemId)
                .ifPresent(judgeTemplateDTO::setProblemId);

        JudgeTemplateDO judgeTemplateDO = judgeTemplateConverter.from(judgeTemplateDTO);
        AssertUtils.isTrue(judgeTemplateDao.save(judgeTemplateDO), ApiExceptionEnum.SERVER_BUSY);

        return judgeTemplateDO.getId();
    }


    public void update(JudgeTemplateDTO judgeTemplateDTO, UserSessionDTO userSessionDTO) {
        JudgeTemplateDO originalJudgeTemplateDO = judgeTemplateDao.getById(judgeTemplateDTO.getId());
        AssertUtils.notNull(originalJudgeTemplateDO, ApiExceptionEnum.JUDGETEMPLATE_NOT_FOUND);
        AssertUtils.isTrue(userSessionDTO.userIdEquals(originalJudgeTemplateDO.getUserId())
                || PermissionEnum.SUPERADMIN.in(userSessionDTO), ApiExceptionEnum.USER_NOT_MATCHING);
        // 构造更新器
        JudgeTemplateDO judgeTemplateDO = judgeTemplateConverter.from(judgeTemplateDTO);
        judgeTemplateDO.setVersion(originalJudgeTemplateDO.getVersion());
        // 对 zipFileId 要更新，不能忽视null
        LambdaUpdateChainWrapper<JudgeTemplateDO> updater = judgeTemplateDao.lambdaUpdate()
                .eq(JudgeTemplateDO::getId, judgeTemplateDO.getId())
                .set(JudgeTemplateDO::getZipFileId, judgeTemplateDO.getZipFileId());
        // 更新
        AssertUtils.isTrue(updater.update(judgeTemplateDO), ApiExceptionEnum.SERVER_BUSY);
    }

    public List<JudgeTemplateListDTO> listByIds(List<Long> judgeTemplateIdList) {
        if (CollectionUtils.isEmpty(judgeTemplateIdList)) {
            return Lists.newArrayList();
        }
        List<JudgeTemplateDO> judgeTemplateManageListDOList = judgeTemplateDao.lambdaQuery().select(
                JudgeTemplateDO::getId,
                JudgeTemplateDO::getType,
                JudgeTemplateDO::getTitle,
                JudgeTemplateDO::getComment,
                JudgeTemplateDO::getAcceptFileExtensions
        ).in(JudgeTemplateDO::getId, judgeTemplateIdList).list();
        return judgeTemplateListConverter.to(judgeTemplateManageListDOList);
    }

    public List<JudgeTemplateManageListDTO> listByTitle(String title, UserSessionDTO userSessionDTO) {
        LambdaQueryChainWrapper<JudgeTemplateManageListDO> query = judgeTemplateManageListDao.lambdaQuery().select(
            JudgeTemplateManageListDO::getId,
            JudgeTemplateManageListDO::getType,
            JudgeTemplateManageListDO::getTitle,
            JudgeTemplateManageListDO::getComment
        ).likeRight(JudgeTemplateManageListDO::getTitle, title);
        // 超级管理员能查所有的，其他只查 public 或自己的
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            Long userId = Optional.ofNullable(userSessionDTO).map(UserSessionDTO::getUserId).orElse(null);
            query.and(o1 -> o1.eq(JudgeTemplateManageListDO::getIsPublic, 1)
                    .or(o2 -> o2.eq(JudgeTemplateManageListDO::getIsPublic, 0)
                            .and(o3 -> o3.eq(JudgeTemplateManageListDO::getUserId, userId))));
        }
        return Optional.ofNullable(judgeTemplateManageListConverter.to(query.list())).orElse(Lists.newArrayList());
    }

    public String idToTitle(Long id) {
        return Optional.ofNullable(judgeTemplateDao.lambdaQuery()
                .select(JudgeTemplateDO::getId, JudgeTemplateDO::getTitle)
                .eq(JudgeTemplateDO::getId, id)
                .one())
            .map(JudgeTemplateDO::getTitle).orElse(null);
    }

    public Integer idToType(Long id) {
        return Optional.ofNullable(judgeTemplateDao.lambdaQuery()
                .select(JudgeTemplateDO::getId, JudgeTemplateDO::getType)
                .eq(JudgeTemplateDO::getId, id)
                .one())
                .map(JudgeTemplateDO::getType).orElse(null);
    }
}
