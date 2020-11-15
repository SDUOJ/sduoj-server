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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
@WebSocketMapping("/submission")
public class SubmissionHandler implements WebSocketHandler {

    public static final int MAX_LISTENING = 100;

    @Resource
    private ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSender>> submissionIdToSenderMap;

    @Resource
    private ConcurrentHashMap<String, WebSocketSender> senderMap;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        InetSocketAddress remoteAddress = handshakeInfo.getRemoteAddress();

        log.info("[submission] id: {}  from: {}", remoteAddress, session.getId());

        Mono<Void> output = session.send(Flux.create(sink -> {
            WebSocketSender sender = new WebSocketSender(session, sink);
            senderMap.put(sender.getId(), sender);
        })).then();

        Mono<Void> input = session.receive()
//                .doOnSubscribe(conn -> {
//                })
                .doOnNext(msg -> {
                    WebSocketSender sender = senderMap.get(session.getId());
                    int size = sender.getListeningIdSet().size();
                    // 限制最多监听 submission 数
                    if (size >= MAX_LISTENING) {
                        return;
                    }
                    String messageText = msg.getPayloadAsText();
                    log.info("[submission] id: {} text: {}", sender.getId(), messageText);
                    List<String> submissionIdHex = JSON.parseObject(messageText, new TypeReference<List<String>>() {});
                    for (String idHex : submissionIdHex) {
                        if (sender.addListening(idHex)) {
                            addNewWebSocketSender(idHex, sender);
                            if (++size >= MAX_LISTENING) {
                                return;
                            }
                        }
                    }
                })
                .doOnComplete(() -> {
                    WebSocketSender sender = senderMap.get(session.getId());
                    sender.getListeningIdSet().forEach(submissionId ->
                        Optional.ofNullable(submissionIdToSenderMap.get(submissionId)).ifPresent(map -> map.remove(session.getId()))
                    );
                })
                .doOnCancel(() -> {
                    WebSocketSender sender = senderMap.get(session.getId());
                    sender.getListeningIdSet().forEach(submissionId ->
                        Optional.ofNullable(submissionIdToSenderMap.get(submissionId)).ifPresent(map -> map.remove(session.getId()))
                    );
                })
                .then();

        return Mono.zip(input, output).then();
    }


    private void addNewWebSocketSender(String submissionId, WebSocketSender webSocketSender) {
        Map<String, WebSocketSender> webSocketSenders = submissionIdToSenderMap.computeIfAbsent(submissionId, k -> new ConcurrentHashMap<>());
        webSocketSenders.put(webSocketSender.getId(), webSocketSender);
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