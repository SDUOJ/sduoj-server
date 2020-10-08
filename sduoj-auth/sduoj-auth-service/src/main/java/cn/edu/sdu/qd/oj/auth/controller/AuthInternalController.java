/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.auth.controller;

import cn.edu.sdu.qd.oj.auth.api.PermissionApi;
import cn.edu.sdu.qd.oj.auth.dto.PermissionDTO;
import cn.edu.sdu.qd.oj.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthInternalController implements PermissionApi {

    @Autowired
    private AuthService authService;

    @Override
    public void sync(List<PermissionDTO> permissionDTOList) {
        if (!permissionDTOList.isEmpty()) {
            authService.syncNewPermissionUrl(permissionDTOList);
        }
    }

    @Override
    public List<PermissionDTO> listAll() {
        return authService.listAll();
    }
}