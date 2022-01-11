/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.gateway.filter;

import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.gateway.util.FilterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;


/**
 * 用户限流过滤器，优先根据用户Id进行限流，其次根据IP+hash(UserAgent)进行限流，需要放置在 LoginFilter 后
 *
 * @author zhaoyifan0528
 * @author zhangt2333
 */
@Slf4j
@Component
public class UserLimitFilter extends GlobalLimitFilter {

    /**
     * Make sure that order is small than StripFilter‘s
     */
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Long userId = Optional.ofNullable(request.getHeaders().getFirst(UserSessionDTO.HEADER_KEY_USERID))
                              .filter(StringUtils::isNumeric)
                              .map(Long::parseLong)
                              .orElse(null);
        String requestUrl = request.getPath().toString();
        String id;
        if (userId != null) {
            id = userId + "-" + requestUrl;
        } else {
            // 前置过滤器 LoginFilter 特意放行
            String realIp = FilterUtils.getRealIp(request);
            String agent = Optional.ofNullable(request.getHeaders().getFirst("User-Agent")).orElse("");
            id = realIp + "-" + agent.hashCode() + "-" + requestUrl;
        }
        // 进行限流过滤
        return super.rateFilter(exchange, chain, requestUrl, id, 60, super.filterProp.getUserLimitPaths());
    }
}
