/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.annotation.web.server.SpringWebSessionConfiguration;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.server.session.WebSessionManager;

import javax.annotation.Resource;

/**
* 配置 org.springframework.web.server.session.CookieWebSessionIdResolver#cookieMaxAge
* 使得 HttpHeader 中的 set-cookie 的 expire 为配置的整型 maxAge 值
*
* @see org.springframework.session.config.annotation.web.server.SpringWebSessionConfiguration;
* @see org.springframework.web.server.session.CookieWebSessionIdResolver
*
* @author zhangt2333
*
**/
@Configuration
@ConditionalOnClass(SpringWebSessionConfiguration.class)
@EnableConfigurationProperties({CookieProperties.class})
public class CookieConfiguration {

    @Autowired
    private CookieProperties cookieProperties;

    @Resource(name = WebHttpHandlerBuilder.WEB_SESSION_MANAGER_BEAN_NAME)
    private WebSessionManager webSessionManager;

    @Autowired
    public void setMaxAge() {
        if (webSessionManager instanceof DefaultWebSessionManager) {
            WebSessionIdResolver sessionIdResolver = ((DefaultWebSessionManager) webSessionManager).getSessionIdResolver();
            if (sessionIdResolver instanceof CookieWebSessionIdResolver) {
                ((CookieWebSessionIdResolver) sessionIdResolver).setCookieMaxAge(cookieProperties.getMaxAge());
            }
        }
    }
}