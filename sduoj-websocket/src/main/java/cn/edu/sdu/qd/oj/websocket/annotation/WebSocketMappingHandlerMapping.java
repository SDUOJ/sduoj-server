/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.websocket.annotation;

import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class WebSocketMappingHandlerMapping extends SimpleUrlHandlerMapping {
	
	private Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();

	/**
     * Register WebSocket handlers annotated by @WebSocketMapping
     * @throws BeansException
     */
    @Override
    public void initApplicationContext() throws BeansException {
        Map<String, Object> beanMap = obtainApplicationContext().getBeansWithAnnotation(WebSocketMapping.class);
        beanMap.values().forEach(bean -> {
            if (!(bean instanceof WebSocketHandler)) {
                throw new RuntimeException(String.format("Controller [%s] doesn't implement WebSocketHandler interface.",
                                bean.getClass().getName()));
            }
            WebSocketMapping annotation = AnnotationUtils.getAnnotation(bean.getClass(), WebSocketMapping.class);
            handlerMap.put(Objects.requireNonNull(annotation).value(), (WebSocketHandler) bean);
        });
        super.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.setUrlMap(handlerMap);
        super.initApplicationContext();
    }
}