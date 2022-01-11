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

import cn.edu.sdu.qd.oj.gateway.config.FilterProperties;
import cn.edu.sdu.qd.oj.gateway.limiter.RequestRateLimiter;
import cn.edu.sdu.qd.oj.gateway.util.FilterUtils;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

/**
 * 限流过滤器
 *
 * @author zhaoyifan0528
 * @author zhangt2333
 */
@Slf4j
@Component
@EnableConfigurationProperties({FilterProperties.class})
public class GlobalLimitFilter implements GlobalFilter, Ordered {

    protected final Set<String> limitedUrls = new ConcurrentHashSet<>();

    @Autowired
    protected FilterProperties filterProp;

    @Autowired
    private RequestRateLimiter requestRateLimiter;

    /**
     * Make sure that this filter takes precedence over StripFilter
     */
    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().toString();
        return rateFilter(exchange, chain, requestUrl, requestUrl, 1, filterProp.getGlobalLimitPaths());
    }

    public Mono<Void> rateFilter(ServerWebExchange exchange,
                                 GatewayFilterChain chain,
                                 String requestUrl,
                                 String id,
                                 int requested,
                                 Map<String, Integer> rateConfig) {
        requestRateLimiter.isAllowed(requestUrl, id, rateConfig, requested).doOnNext(allowed -> {
            if (!allowed) {
                limitedUrls.add(requestUrl);
            } else {
                limitedUrls.remove(requestUrl);
            }
        }).subscribe();

        if (limitedUrls.contains(requestUrl)) {
            log.info("{}Limit for url: {} from: {}", requested < 2 ? "Global" : "User", requestUrl, id);
            return FilterUtils.returnWithStatus(exchange, HttpStatus.TOO_MANY_REQUESTS,
                                           "您的操作请求过于频繁，已被服务器限流，请稍等片刻后重试！！（如为异常误报，请联系管理员）");
        }
        return chain.filter(exchange);
    }
}
