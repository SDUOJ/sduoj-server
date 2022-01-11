/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.gateway.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class FilterUtils {

    public static String getRealIp(ServerWebExchange exchange) {
        return getRealIp(exchange.getRequest());
    }

    public static String getRealIp(ServerHttpRequest request) {
        String realIp = Optional.ofNullable(request.getHeaders().getFirst("x-real-ip"))
                                .orElse(String.valueOf(request.getRemoteAddress()));
        int index = realIp.indexOf(':');
        if (index != -1) {
            realIp = realIp.substring(0, index);
        }
        return realIp;
    }

    public static Mono<Void> returnWithStatus(ServerWebExchange exchange,
                                              HttpStatus httpStatus,
                                              String msg) {
        // 返回鉴权失败的消息
        JSONObject message = new JSONObject();
        message.put("code", httpStatus.value());
        message.put("message", msg);
        message.put("timestamp", String.valueOf(System.currentTimeMillis()));
        message.put("data", null);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(message))));
    }
}
