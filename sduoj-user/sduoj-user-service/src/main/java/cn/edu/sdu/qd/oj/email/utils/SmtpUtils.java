/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.email.utils;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.email.dto.EmailMessageDTO;
import cn.edu.sdu.qd.oj.email.config.MailPropertiesConfiguration;
import cn.edu.sdu.qd.oj.user.config.UserServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * use smtp to send email
 * @author zhangt2333
 */
@Slf4j
@Component
@EnableConfigurationProperties({UserServiceProperties.class})
public class SmtpUtils {

    @Autowired
    private MailPropertiesConfiguration mailPropertiesConfiguration;

    public boolean isEmailEnable() {
        return mailPropertiesConfiguration.getJavaMailSender() != null;
    }

    public MimeMessage packageMail(EmailMessageDTO messageDTO) throws MessagingException {
        JavaMailSender javaMailSender = mailPropertiesConfiguration.getJavaMailSender();
        if (javaMailSender == null) {
            throw new ApiException(ApiExceptionEnum.NONE_EMAIL_SENDER);
        }
        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true, "utf-8");
        helper.setSubject(messageDTO.getSubject());
        helper.setTo(messageDTO.getTo());
        helper.setFrom(mailPropertiesConfiguration.getMailProperties().getUsername());
        helper.setText(messageDTO.getText(), true);
        return mail;
    }

    public void send(MimeMessage mail) {
        try {
            mailPropertiesConfiguration.getJavaMailSender().send(mail);
        } catch (Exception e) {
            log.error("", e);
            throw new ApiException(ApiExceptionEnum.EMAIL_SEND_FAILED);
        }
    }
}