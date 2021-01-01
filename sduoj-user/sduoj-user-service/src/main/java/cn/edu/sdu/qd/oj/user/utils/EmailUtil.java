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

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    static final String EMAIL_VERIFY_PATTERN =
                    "<html>" +
                    "  <body>" +
                    "  <h2>Dear %s, </h2>" +
                    "  <br>" +
                    "  请点击以下链接对你的账号进行验证" +
                    "  <br>" +
                    "  <div style=\"padding: 12px; background-color: #E0E0E0; color: #000000; font-weight: bold; font-size: 16px;\" align=\"center\">" +
                    "      <a href=\"%s\">%s</a>" +
                    "  </div>" +
                    "<div style=\"text-align: center;padding: 50px 50px 24px;color: #515a6e;font-size: 14px;\">2020-2020 &copy; Shandong University</div>" +
                    "  </body>" +
                    "</html>";

    static final String FORGET_PASSWORD_PATTERN =
            "<html>" +
                    "  <body>" +
                    "  <h2>Dear %s, </h2>" +
                    "  <br>" +
                    "  请点击以下链接对你的账号进行重置密码" +
                    "  <br>" +
                    "  <div style=\"padding: 12px; background-color: #E0E0E0; color: #000000; font-weight: bold; font-size: 16px;\" align=\"center\">" +
                    "      <a href=\"%s\">%s</a>" +
                    "  </div>" +
                    "<div style=\"text-align: center;padding: 50px 50px 24px;color: #515a6e;font-size: 14px;\">2020-2020 &copy; Shandong University</div>" +
                    "  </body>" +
                    "</html>";

    static final String RESET_EMAIL_PATTERN =
            "<html>" +
                    "  <body>" +
                    "  <h2>Dear %s, </h2>" +
                    "  <br>" +
                    "  请点击以下链接对你的账号进行重设邮箱验证" +
                    "  <br>" +
                    "  <div style=\"padding: 12px; background-color: #E0E0E0; color: #000000; font-weight: bold; font-size: 16px;\" align=\"center\">" +
                    "      <a href=\"%s\">%s</a>" +
                    "  </div>" +
                    "<div style=\"text-align: center;padding: 50px 50px 24px;color: #515a6e;font-size: 14px;\">2020-2020 &copy; Shandong University</div>" +
                    "  </body>" +
                    "</html>";

    public boolean isEmailEnable() {
        return javaMailSender != null;
    }

    public void sendResetEmailMail(String username, @Email String email, String token) throws MessagingException {
        String subject = userServiceProperties.getResetEmailSubject();
        String url = userServiceProperties.getVerificationUrlPrefix() + token;
        String text = String.format(RESET_EMAIL_PATTERN, username, url, url);
        MimeMessage mail = packageMail(email, subject, text);
        send(mail);
    }

    public void sendVerificationEmail(String username, @Email String email, String token) throws MessagingException {
        String subject = userServiceProperties.getVerificationEmailSubject();
        String url = userServiceProperties.getVerificationUrlPrefix() + token;
        String text = String.format(EMAIL_VERIFY_PATTERN, username, url, url);
        MimeMessage mail = packageMail(email, subject, text);
        send(mail);
    }

    public void sendForgetPasswordEmail(String username, @Email String email, String token) throws MessagingException {
        String subject = userServiceProperties.getForgetPasswordEmailSubject();
        String url = userServiceProperties.getForgetPasswordUrlPrefix() + token;
        String text = String.format(FORGET_PASSWORD_PATTERN, username, url, url);
        MimeMessage mail = packageMail(email, subject, text);
        send(mail);
    }

    private MimeMessage packageMail(@Email String email, String subject, String text) throws MessagingException {
        if (javaMailSender == null) {
            throw new ApiException(ApiExceptionEnum.NONE_EMAIL_SENDER);
        }
        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setSubject(subject);
        helper.setTo(email);
        helper.setFrom(userServiceProperties.getFromEmail());
        helper.setText(text, true);
        return mail;
    }

    @Async
    public void send(MimeMessage mail) {
        try {
            javaMailSender.send(mail);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.EMAIL_SEND_FAILED);
        }
    }
}