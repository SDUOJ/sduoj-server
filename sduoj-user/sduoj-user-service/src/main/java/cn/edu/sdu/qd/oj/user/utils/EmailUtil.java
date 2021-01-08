/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.utils;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.user.config.MailPropertiesConfiguration;
import cn.edu.sdu.qd.oj.user.config.UserServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Email;

@Slf4j
@Component
@EnableConfigurationProperties({UserServiceProperties.class})
public class EmailUtil {

    @Autowired
    private UserServiceProperties userServiceProperties;

    @Autowired
    private MailPropertiesConfiguration mailPropertiesConfiguration;

    public boolean isEmailEnable() {
        return mailPropertiesConfiguration.getJavaMailSender() != null;
    }

    public void sendResetEmailMail(String username, @Email String email, String token) throws MessagingException {
        String subject = userServiceProperties.getResetEmailSubject();
        String url = userServiceProperties.getVerifyEmailUrlPrefix() + token;
        String text = String.format(userServiceProperties.getResetEmailPattern(), username, url, url);
        MimeMessage mail = packageMail(email, subject, text);
        send(mail);
    }

    public void sendVerificationEmail(String username, @Email String email, String token) throws MessagingException {
        String subject = userServiceProperties.getVerifyEmailSubject();
        String url = userServiceProperties.getVerifyEmailUrlPrefix() + token;
        String text = String.format(userServiceProperties.getVerifyEmailPattern(), username, url, url);
        MimeMessage mail = packageMail(email, subject, text);
        send(mail);
    }

    public void sendForgetPasswordEmail(String username, @Email String email, String token) throws MessagingException {
        String subject = userServiceProperties.getForgetPasswordEmailSubject();
        String url = userServiceProperties.getForgetPasswordUrlPrefix() + token;
        String text = String.format(userServiceProperties.getForgetPasswordPattern(), username, url, url);
        MimeMessage mail = packageMail(email, subject, text);
        send(mail);
    }

    private MimeMessage packageMail(@Email String email, String subject, String text) throws MessagingException {
        JavaMailSender javaMailSender = mailPropertiesConfiguration.getJavaMailSender();
        if (javaMailSender == null) {
            throw new ApiException(ApiExceptionEnum.NONE_EMAIL_SENDER);
        }
        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setSubject(subject);
        helper.setTo(email);
        helper.setFrom(mailPropertiesConfiguration.getMailProperties().getUsername());
        helper.setText(text, true);
        return mail;
    }

    @Async
    public void send(MimeMessage mail) {
        try {
            mailPropertiesConfiguration.getJavaMailSender().send(mail);
        } catch (Exception e) {
            log.error("", e);
            throw new ApiException(ApiExceptionEnum.EMAIL_SEND_FAILED);
        }
    }
}