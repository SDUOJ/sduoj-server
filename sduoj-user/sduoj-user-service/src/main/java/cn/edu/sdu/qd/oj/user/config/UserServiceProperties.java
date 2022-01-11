/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 基于 Nacos 后可动态配置的 properties
 * @author zhangt2333
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sduoj.user")
public class UserServiceProperties {

    /**
     * 是否强制邮箱验证
     * 如果要强制验证而邮件服务未配置则用户侧会无法正常使用注册、更改邮箱等服务
     * 如果不强制验证, 则用户不需要输入正确的 emailCode 就能通过服务, 可能会导致数据库中的 email 是没验证过的
     */
    private boolean enableEmailVerification = true;

    /**
     * 是否打开发送邮箱验证码
     * 如果打开会尝试发邮件, 具体在使用 emailCode 时验不验证则看 UserServiceProperties#enableEmailVerification
     * 如果不打开会根据 UserServiceProperties#enableEmailVerification 的值提示用户
     */
    private boolean enableSendingEmailCode = true;

    /**
     * 发邮件的时间间隔
     */
    private int sendEmailInterval = 60;

    /**
     * 邮件验证码有效期
     */
    private int verificationExpire = 60 * 5;

    /**
     * 验证邮箱邮件主题
     */
    private String verifyEmailSubject;

    /**
     * 验证邮箱邮件内容
     */
    private String verifyEmailPattern;

    /**
     * 忘记密码邮件主题
     */
    private String forgetPasswordEmailSubject;

    /**
     * 忘记密码链接前缀
     */
    private String forgetPasswordUrlPrefix;

    /**
     * 忘记密码邮件内容
     */
    private String forgetPasswordPattern;

    /**
     * 重设邮箱邮件主题
     */
    private String resetEmailSubject;

    /**
     * 重设邮箱邮件内容
     */
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