/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.user.dto.UserBatchAddDTO;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.dto.UserListReqDTO;
import cn.edu.sdu.qd.oj.user.dto.UserManageUpdateReqDTO;
import cn.edu.sdu.qd.oj.user.service.UserManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private UserManageService userManageService;

    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<UserDTO> list(UserListReqDTO reqDTO,
                                    @UserSession UserSessionDTO userSessionDTO) {
        PageResult<UserDTO> pageResult = userManageService.list(reqDTO);
        // TODO: 根据 superadmin、admin 权限进行脱敏
        return pageResult;
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void update(@RequestBody UserManageUpdateReqDTO reqDTO,
                       @UserSession UserSessionDTO userSessionDTO) {
        // superadmin 才能改密码+改权限
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            reqDTO.setPassword(null);
            reqDTO.setRoles(null);
        }
        userManageService.update(reqDTO, userSessionDTO);
        return null;
    }

    @PostMapping("/addUsers")
    @ApiResponseBody
    public Void addUsers(@RequestBody @Valid List<UserBatchAddDTO> userDTOList,
                         @UserSession UserSessionDTO userSessionDTO) {
        userManageService.addUsers(userDTOList);
        return null;
    }

    @PostMapping("/delete")
    @ApiResponseBody
    public Void delete(@RequestBody List<String> usernameList,
                       @UserSession UserSessionDTO userSessionDTO) {
        AssertUtils.isTrue(PermissionEnum.SUPERADMIN.in(userSessionDTO), ApiExceptionEnum.USER_NOT_MATCHING);
        userManageService.delete(usernameList);
        return null;
    }
}