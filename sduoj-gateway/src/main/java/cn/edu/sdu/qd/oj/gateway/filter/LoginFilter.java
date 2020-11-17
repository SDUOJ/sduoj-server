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

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.gateway.client.PermissionClient;
import cn.edu.sdu.qd.oj.gateway.client.UserClient;
import cn.edu.sdu.qd.oj.gateway.config.FilterProperties;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @ClassName LoginFilter
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/21 21:16
 * @Version V1.0
 **/

@Slf4j
@Component
@EnableConfigurationProperties({FilterProperties.class})
public class LoginFilter implements GlobalFilter, Ordered {

    @Autowired
    private FilterProperties filterProp;

    @Autowired
    private PermissionClient permissionClient;

    @Autowired
    private UserClient userClient;

    /**
     * @Description TODO
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     **/
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().toString();
        log.info("filter url:{}", requestUrl);
        // 取 token 并解密
        UserSessionDTO userSessionDTO = Optional.of(exchange)
                                    .map(ServerWebExchange::getSession)
                                    .map(Mono::block)
                                    .map(WebSession::getAttributes)
                                    .map(map -> map.get("SDUOJUserInfo"))
                                    .map(o -> (String) o)
                                    .map(o -> JSON.parseObject(o, UserSessionDTO.class))
                                    .orElse(null);
        // 无 session，非 allowUrl
        if (userSessionDTO == null && !isAllowPath(requestUrl)) {
            return returnNoPermission(exchange, " 你的账号没有该权限或未登录! ");
        }

        // 鉴权
        if (userSessionDTO != null) {

            List<String> urlRoles = Optional.ofNullable(permissionClient.urlToRoles(requestUrl.replace("/api", ""))).orElse(Lists.newArrayList());
            List<String> roles = Optional.ofNullable(userClient.queryRolesById(userSessionDTO.getUserId())).orElse(Lists.newArrayList());

            if (!urlRoles.contains(PermissionEnum.ALL.name) && Collections.disjoint(roles, urlRoles)) {
                log.warn("have not permission {} {}", userSessionDTO, requestUrl);
                return returnNoPermission(exchange, String.format("This User has no permission on '%s'", requestUrl));
            }


            // 装饰器 修改 getHeaders 方法
            ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public HttpHeaders getHeaders() {
                    MultiValueMap<String, String> multiValueMap = CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap(8, Locale.ENGLISH));
                    super.getHeaders().forEach((key, value) -> multiValueMap.put(key, value));
    //              multiValueMap.remove("cookie"); // 在此处已解码 token, 故不下传省流量, 如果后续有多值 cookie 此处需要修改
                    multiValueMap.remove("SDUOJUserInfo");
                    multiValueMap.add("SDUOJUserInfo", JSON.toJSONString(userSessionDTO));
                    for (Field field : UserSessionDTO.class.getDeclaredFields()) {
                        try {
                            field.setAccessible(true);
                            multiValueMap.remove("Authorization-" + field.getName());
                            multiValueMap.add("Authorization-" + field.getName(), String.valueOf(field.get(userSessionDTO)));
                        } catch (IllegalAccessException e) {
                            log.error("getHeaders Decorator", e);
                        }
                    }
                    return new HttpHeaders(multiValueMap);
                }
            };
            return chain.filter(exchange.mutate().request(decorator).build()).then(thenHandlerSession(exchange));
        }
        return chain.filter(exchange).then(thenHandlerSession(exchange));
    }

    private Mono<Void> thenHandlerSession(ServerWebExchange exchange) {
        return Mono.fromRunnable(() -> {
            List<String> userInfos = exchange.getResponse().getHeaders().remove("SDUOJUserInfo");
            Optional.of(userInfos).filter(list -> !list.isEmpty()).map(list -> list.get(0)).ifPresent(userInfoStr -> {
                Optional.of(exchange).map(ServerWebExchange::getSession).map(Mono::block).map(WebSession::getAttributes).ifPresent(map -> {
                    if ("Logout".equals(userInfoStr)) {
                        map.remove("SDUOJUserInfo");
                    } else {
                        map.put("SDUOJUserInfo", userInfoStr);
                    }
                });
            });
        });
    }

    private Mono<Void> returnNoPermission(ServerWebExchange exchange, String msg) {
        // 返回鉴权失败的消息
        ServerHttpResponse response = exchange.getResponse();
        Map<String, Object> message = new HashMap<>();
        message.put("code", HttpStatus.UNAUTHORIZED.value());
        message.put("message", msg);
        message.put("timestamp", String.valueOf(System.currentTimeMillis()));
        message.put("data", null);
        byte[] bits = JSON.toJSONBytes(message);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    private boolean isAllowPath(String requestUrl) {
        for (String allowPath : filterProp.getAllowPaths())
            if (requestUrl.startsWith(allowPath))
                return true;
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}