/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.gateway.filter;

import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.SocketException;
import java.util.regex.Pattern;

/**
* @Description 对出错 response 进行规整，未出错的业务返回无需处理
**/
@Slf4j
@Order(-1)
@Configuration
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public static final Pattern PATTERN_REPLACE_HOST = Pattern.compile("([0-9\\.]+:[0-9]*)");

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable t) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(t);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        int statusCode = t instanceof ResponseStatusException ? ((ResponseStatusException) t).getStatus().value() : 500;
        String message = t instanceof SocketException ? PATTERN_REPLACE_HOST.matcher(t.getMessage()).replaceFirst(""): t.getMessage();
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(ResponseResult.fail(statusCode, message)));
            } catch (JsonProcessingException e) {
                log.error("", e);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}