/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.service;

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
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
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        Page<JudgeTemplateManageListDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        List<JudgeTemplateManageListDTO> judgeTemplateManageListDTOList = judgeTemplateManageListConverter.to(pageResult.getRecords());
        return new PageResult<>(pageResult.getPages(), judgeTemplateManageListDTOList);
    }

    public Long create(JudgeTemplateDTO judgeTemplateDTO, UserSessionDTO userSessionDTO) {
        JudgeTemplateDO judgeTemplateDO = judgeTemplateConverter.from(judgeTemplateDTO);
        AssertUtils.isTrue(judgeTemplateDao.save(judgeTemplateDO), ApiExceptionEnum.SERVER_BUSY);

        return judgeTemplateDO.getId();
    }


    public void update(JudgeTemplateDTO judgeTemplateDTO, UserSessionDTO userSessionDTO) {
        JudgeTemplateDO judgeTemplateDO = judgeTemplateDao.getById(judgeTemplateDTO.getId());
        AssertUtils.notNull(judgeTemplateDO, ApiExceptionEnum.JUDGETEMPLATE_NOT_FOUND);
        AssertUtils.isTrue(userSessionDTO.userIdEquals(judgeTemplateDO.getUserId())
                || PermissionEnum.SUPERADMIN.in(userSessionDTO), ApiExceptionEnum.USER_NOT_MATCHING);
        AssertUtils.isTrue(judgeTemplateDao.updateById(judgeTemplateDO), ApiExceptionEnum.SERVER_BUSY);
    }

    public List<JudgeTemplateListDTO> listByIds(List<Long> judgeTemplateIdList) {
        List<JudgeTemplateDO> judgeTemplateManageListDOList = judgeTemplateDao.lambdaQuery().select(
                JudgeTemplateDO::getId,
                JudgeTemplateDO::getType,
                JudgeTemplateDO::getTitle,
                JudgeTemplateDO::getAcceptFileExtensions
        ).in(JudgeTemplateDO::getId, judgeTemplateIdList).list();
        return judgeTemplateListConverter.to(judgeTemplateManageListDOList);
    }
}
