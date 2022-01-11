/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.websocket.handler;

import cn.edu.sdu.qd.oj.websocket.entity.WebSocketSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RedisListenHandler extends MessageListenerAdapter {

    @Resource
    private ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSender>> submissionIdToSenderMap;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        super.onMessage(message, pattern);
        String patternStr = new String(pattern);
        String channelStr = new String(message.getChannel());
        // TODO: better way to extract 'key'
        String submissionId = channelStr.substring(channelStr.lastIndexOf('/') + 1);
        String msg = new String(message.getBody());
        log.info("{} {} {}", patternStr, channelStr, msg);
        ConcurrentHashMap<String, WebSocketSender> webSocketSenderMap = submissionIdToSenderMap.get(submissionId);
        if (webSocketSenderMap != null) {
            webSocketSenderMap.forEach((k, v) -> {
                v.sendData(msg);
            });
        }
    }
}