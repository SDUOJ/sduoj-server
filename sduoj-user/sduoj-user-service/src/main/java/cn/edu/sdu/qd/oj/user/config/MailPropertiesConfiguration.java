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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
* @Description Auto-configure a refreshable {@link MailSender} based on properties configuration.
* from org.springframework.boot.autoconfigure.mail.MailSenderPropertiesConfiguration
**/
@Setter
@Component
@RefreshScope
@EnableConfigurationProperties({MailProperties.class})
public class MailPropertiesConfiguration {

    @Getter
    @Autowired
    private MailProperties mailProperties;

    private Integer lastMailPropertiesHashCode;

    private JavaMailSender javaMailSender;

    public JavaMailSender getJavaMailSender() {
        if (StringUtils.isBlank(mailProperties.getUsername()) || !mailProperties.isEnable()) {
            if (!mailProperties.isEnable()) {
                javaMailSender = null;
            }
            return null;
        }
        if (javaMailSender == null || !Objects.equals(lastMailPropertiesHashCode, mailProperties.hashCode())) {
            synchronized (MailPropertiesConfiguration.class) {
                if (javaMailSender == null || !Objects.equals(lastMailPropertiesHashCode, mailProperties.hashCode())) {
                    JavaMailSenderImpl sender = new JavaMailSenderImpl();
                    applyProperties(sender);
                    javaMailSender = sender;
                    lastMailPropertiesHashCode = mailProperties.hashCode();
                }
            }
        }
        return javaMailSender;
    }


    private void applyProperties(JavaMailSenderImpl sender) {
        sender.setHost(mailProperties.getHost());
        if (mailProperties.getPort() != null) {
            sender.setPort(mailProperties.getPort());
        }
        sender.setUsername(mailProperties.getUsername());
        sender.setPassword(mailProperties.getPassword());
        sender.setProtocol(mailProperties.getProtocol());
        if (mailProperties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(mailProperties.getDefaultEncoding().name());
        }
        if (!mailProperties.getProperties().isEmpty()) {
            sender.setJavaMailProperties(asProperties(mailProperties.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }
}
