/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;


@Data
@EqualsAndHashCode
@AllArgsConstructor
public class WebSocketSender {

    private String uuid;
    private WebSocketSession session;
    private FluxSink<WebSocketMessage> sink;

    public void sendData(String data) {
        sink.next(session.textMessage(data));
    }
}