/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.email.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于 Nacos 可动态配置
 * from org.springframework.boot.autoconfigure.mail.MailProperties
 * @author zhangt2333
 */
@Getter
@Setter
@EqualsAndHashCode
@ConfigurationProperties(prefix = "sduoj.mail")
public class MailProperties {

    private boolean enable = false;
    private String username;
    private String password;
    private String host;
    private Integer port;
    private String protocol = "smtp";
    private Charset defaultEncoding = StandardCharsets.UTF_8;
    private Map<String, String> properties = new HashMap<>();
}
