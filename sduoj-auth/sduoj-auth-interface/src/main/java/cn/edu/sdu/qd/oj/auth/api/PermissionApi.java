/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.auth.api;

import cn.edu.sdu.qd.oj.auth.dto.PermissionDTO;
import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/internal/auth")
public interface PermissionApi {
    String SERVICE_NAME = "auth-service";

    /**
    * @Description 同步微服务 URL 到权限中心
    * @param permissionDTOList
    * @return void
    **/
    @PostMapping(value = "/sync", consumes = "application/json")
    void sync(@RequestBody List<PermissionDTO> permissionDTOList);

    /**
    * @Description 查询所有url权限信息
    **/
    @GetMapping(value = "/listAll")
    @Cacheable(value = RedisConstants.ALL_URL_PERMISSION)
    List<PermissionDTO> listAll();

    @GetMapping(value = "/queryUrlToRolesMap")
    @Cacheable(value = RedisConstants.URL_TO_ROLES_MAP)
    Map<String, List<String>> queryUrlToRolesMap();

    @GetMapping(value = "/urlToRoles")
    @Cacheable(key = "#url", value = RedisConstants.URL_TO_ROLES)
    List<String> urlToRoles(@RequestParam("url") String url);
}