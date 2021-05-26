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

    /**
    * 邮件验证码有效期
    **/
    private int verificationExpire;

    /**
     * 验证邮箱链接前缀
     **/
    private String verifyEmailUrlPrefix;

    /**
     * 验证邮箱邮件主题
     **/
    private String verifyEmailSubject;

    /**
     * 验证邮箱邮件内容
     **/
    private String verifyEmailPattern;

    /**
     * 忘记密码邮件主题
     **/
    private String forgetPasswordEmailSubject;

    /**
     * 忘记密码链接前缀
     **/
    private String forgetPasswordUrlPrefix;

    /**
     * 忘记密码邮件内容
     **/
    private String forgetPasswordPattern;

    /**
     * 重设邮箱邮件主题
     **/
    private String resetEmailSubject;

    /**
     * 重设邮箱邮件内容
     **/
    private String resetEmailPattern;

    /**
     * SDUCAS 第三方登录开启
     */
    private boolean enableThirdPartySduCas = false;

    /**
     * SDU CAS service URL
     */
    private String sduCasServiceUrl;
}