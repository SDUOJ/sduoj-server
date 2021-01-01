/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sduoj.user")
public class UserServiceProperties {

    private String verificationUrlPrefix;
    private String forgetPasswordUrlPrefix;
    private int verificationExpire;
    private String fromEmail;
    private String verificationEmailSubject;
    private String forgetPasswordEmailSubject;
    private String resetEmailSubject;
}