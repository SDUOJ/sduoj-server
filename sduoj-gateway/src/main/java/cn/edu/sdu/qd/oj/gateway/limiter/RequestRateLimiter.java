/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.gateway.limiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.validation.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class RequestRateLimiter extends RedisRateLimiter {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private RedisScript<List<Long>> script;

    public RequestRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                              RedisScript<List<Long>> script,
                              Validator validator) {
        super(redisTemplate, script, validator);
        this.script = script;
        initialized.compareAndSet(false, true);
    }

    @SuppressWarnings("unchecked")
    public Mono<Boolean> isAllowed(String routeId, String id, Map<String, Integer> config, int requested) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("RedisRateLimiter is not initialized");
        }

        int replenishRate = Optional.ofNullable(config.get(routeId))
                .orElseGet(() -> config.getOrDefault("default", 10000));
        int capacity = Math.max(replenishRate, requested);
        try {
            List<String> scriptArgs = Stream.of(replenishRate, capacity, Instant.now().getEpochSecond(), requested)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            Flux<List<Long>> flux = this.redisTemplate.execute(this.script, getKeys(id), scriptArgs);
            return flux.onErrorResume(throwable -> Flux.just(Arrays.asList(1L, -1L)))
                    .reduce(new ArrayList<Long>(), (longs, l) -> {
                        longs.addAll(l);
                        return longs;
                    }).map(results -> results.get(0) == 1L);
        } catch (Exception e) {
            log.error("Error determining if user allowed from redis", e);
        }
        return Mono.just(true);
    }

    static List<String> getKeys(String id) {
        String prefix = "request_rate_limiter.{" + id;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }
}
