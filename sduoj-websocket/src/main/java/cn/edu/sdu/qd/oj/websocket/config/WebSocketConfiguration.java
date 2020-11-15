/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.websocket.config;

import cn.edu.sdu.qd.oj.websocket.annotation.WebSocketMappingHandlerMapping;
import cn.edu.sdu.qd.oj.websocket.entity.WebSocketSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class WebSocketConfiguration {
 
	@Bean
	public HandlerMapping webSocketMapping() {
		return new WebSocketMappingHandlerMapping();
	}
 
	@Bean
	public ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSender>> submissionIdToSenderMap() {
		return new ConcurrentHashMap<>();
	}

	@Bean
	public ConcurrentHashMap<String, WebSocketSender> senderMap() {
		return new ConcurrentHashMap<>();
	}
 
	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter();
	}
}