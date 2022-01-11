/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.site.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.site.config.SiteProperties;
import cn.edu.sdu.qd.oj.user.config.UserServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/site")
@Slf4j
@EnableConfigurationProperties({SiteProperties.class})
public class SitePropertyController {
    @Autowired
    private SiteProperties siteProperties;

    @GetMapping("/getCopyright")
    @ApiResponseBody
    public String getCopyright() {
        return siteProperties.getCopyright();
    }

}