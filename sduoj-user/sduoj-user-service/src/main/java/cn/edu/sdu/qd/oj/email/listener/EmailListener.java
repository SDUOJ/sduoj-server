/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.email.listener;

import cn.edu.sdu.qd.oj.email.dto.EmailMessageDTO;
import cn.edu.sdu.qd.oj.email.utils.SmtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;


/**
 * sendingEmail MQ message listener
 * @author zhangt2333
 */
@Slf4j
@Component
public class EmailListener {

    @Autowired
    private SmtpUtils smtpUtils;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "sduoj.email.send", durable = "true"),
            exchange = @Exchange(value = "sduoj.email", ignoreDeclarationExceptions = "true", type = ExchangeTypes.DIRECT),
            key = {"send"}
    ))
    public void handleSubmissionMessage(EmailMessageDTO messageDTO) throws Throwable {
        log.info("emailHash: {}, to: {}, subject: {}", messageDTO.hashCode(), messageDTO.getTo(), messageDTO.getSubject());
        // TODO: 第二方案, 在 smtp 失败后用阿里云邮件推送
        MimeMessage mimeMessage = smtpUtils.packageMail(messageDTO);
        smtpUtils.send(mimeMessage);
    }
}