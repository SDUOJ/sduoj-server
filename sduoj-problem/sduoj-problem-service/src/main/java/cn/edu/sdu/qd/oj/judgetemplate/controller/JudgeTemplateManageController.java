/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.controller;

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateConfigDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateListDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateManageListDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplatePageReqDTO;
import cn.edu.sdu.qd.oj.judgetemplate.enums.JudgeTemplateTypeEnum;
import cn.edu.sdu.qd.oj.judgetemplate.service.JudgeTemplateService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("/manage/judgetemplate")
public class JudgeTemplateManageController {

    @Autowired
    private JudgeTemplateService judgeTemplateService;

    @GetMapping("/query")
    @ApiResponseBody
    public JudgeTemplateDTO query(@RequestParam("id") Long id,
                                  @UserSession UserSessionDTO userSessionDTO) {
        JudgeTemplateDTO judgeTemplateDTO = judgeTemplateService.query(id);
        if (PermissionEnum.SUPERADMIN.in(userSessionDTO)) {
            return judgeTemplateDTO;
        }
        if (!userSessionDTO.userIdEquals(judgeTemplateDTO.getUserId())) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_MATCHING);
        }
        return judgeTemplateDTO;
    }

    @GetMapping("/page")
    @ApiResponseBody
    public PageResult<JudgeTemplateManageListDTO> page(@NotNull JudgeTemplatePageReqDTO reqDTO,
                                                       @UserSession UserSessionDTO userSessionDTO) {
        return judgeTemplateService.page(reqDTO, userSessionDTO);
    }

    @GetMapping("/list")
    @ApiResponseBody
    public List<JudgeTemplateListDTO> list(@RequestParam("type") int type,
                                           @RequestParam(value = "problemCode", required = false) String problemCode,
                                           @UserSession UserSessionDTO userSessionDTO) {
        return judgeTemplateService.list(type, problemCode, userSessionDTO);
    }

    @PostMapping("/create")
    @ApiResponseBody
    public Long create(@RequestBody JudgeTemplateDTO judgeTemplateDTO,
                       @UserSession UserSessionDTO userSessionDTO) {
        validateShellScript(judgeTemplateDTO);
        judgeTemplateDTO.setUserId(userSessionDTO.getUserId());
        return judgeTemplateService.create(judgeTemplateDTO, userSessionDTO);
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void update(@RequestBody JudgeTemplateDTO judgeTemplateDTO,
                       @UserSession UserSessionDTO userSessionDTO) {
        validateShellScript(judgeTemplateDTO);
        judgeTemplateService.update(judgeTemplateDTO, userSessionDTO);
        return null;
    }

    @GetMapping("/listByTitle")
    @ApiResponseBody
    public List<JudgeTemplateManageListDTO> listByTitle(@RequestParam("title") String title,
                                                        @UserSession UserSessionDTO userSessionDTO) {
        if (StringUtils.isBlank(title)) {
            return Lists.newArrayList();
        }
        return judgeTemplateService.listByTitle(title, userSessionDTO);
    }

    /**
    * @Description 校验 jt 的 JSON 格式
    **/
    private void validateShellScript(JudgeTemplateDTO judgeTemplateDTO) {
        JudgeTemplateTypeEnum type = JudgeTemplateTypeEnum.of(judgeTemplateDTO.getType());
        if (JudgeTemplateTypeEnum.IO == type || JudgeTemplateTypeEnum.SPJ == type) {
            try {
                JSON.parseObject(judgeTemplateDTO.getShellScript(), JudgeTemplateConfigDTO.class);
            } catch (Exception e) {
                 throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR, "JSON 格式错误");
            }
        }
    }
}
