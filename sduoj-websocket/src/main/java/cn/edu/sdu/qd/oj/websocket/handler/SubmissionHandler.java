/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.websocket.handler;

import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.websocket.annotation.WebSocketMapping;
import cn.edu.sdu.qd.oj.websocket.constant.SubmissionBizContant;
import cn.edu.sdu.qd.oj.websocket.entity.WebSocketSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@WebSocketMapping("/submission")
@Component
@Slf4j
public class SubmissionHandler implements WebSocketHandler {

    @Autowired
    private ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSender>> senderMap;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        InetSocketAddress remoteAddress = handshakeInfo.getRemoteAddress();
        String params = handshakeInfo.getUri().getQuery();
        Map<String, String> paramMap = decodeParamMap(params);

        String submissionId = paramMap.get("id");

        log.info("submission sub: {} from {}", submissionId, remoteAddress);

        String uuid = UUID.randomUUID().toString();

        Mono<Void> input = session.receive()
//                .doOnSubscribe(conn -> {
//                })
//                .doOnNext(msg -> {
//                })
                .doOnComplete(() -> {
                    Optional.ofNullable(senderMap.get(submissionId)).ifPresent(map -> map.remove(uuid));
                })
                .doOnCancel(() -> {
                    Optional.ofNullable(senderMap.get(submissionId)).ifPresent(map -> map.remove(uuid));
                })
                .then();

        Mono<Void> output = session.send(Flux.create(sink -> addNewWebSocketSender(submissionId, new WebSocketSender(uuid, session, sink))));

        return Mono.zip(input, output).then();
    }


    private void addNewWebSocketSender(String submissionId, WebSocketSender webSocketSender) {
        Map<String, WebSocketSender> webSocketSenders = senderMap.computeIfAbsent(submissionId, k -> new ConcurrentHashMap<>());
        webSocketSenders.put(webSocketSender.getUuid(), webSocketSender);
        List<Object> submissionResults = redisUtils.lGetAll(SubmissionBizContant.getRedisSubmissionKey(submissionId));
        log.info("get submissionResults from redis {} {}", submissionId, submissionResults);
        if (submissionResults != null) {
            webSocketSender.sendData(submissionResults.toString());
        }
    }


    /**
     * @Description 转换 URL Params 为 Map，
     **/
    public static Map<String, String> decodeParamMap(String queryStr) {
        Map<String, String> map = new HashMap<>();
        int len = queryStr.length();
        String name = null;
        int pos = 0;
        int i;
        for (i = 0; i < len; ++i) {
            char c = queryStr.charAt(i);
            switch (c) {
                case '&':
                    map.put(name, queryStr.substring(pos, i));
                    name = null;
                    if (i + 4 < len && "amp;".equals(queryStr.substring(i + 1, i + 5))) {
                        i += 4;
                    }
                    pos = i + 1;
                    break;
                case '=':
                    if (null == name) {
                        name = queryStr.substring(pos, i);
                        pos = i + 1;
                    }
            }
        }
        map.put(name, queryStr.substring(pos, i));
        return map;
    }
}