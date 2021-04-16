/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.gateway.config;

import cn.edu.sdu.qd.oj.gateway.limiter.RequestRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.validation.Validator;

import java.util.List;

@Slf4j
@Configuration
public class RateLimiterConfig {
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public RequestRateLimiter globalRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                                                @Qualifier(RedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript,
                                                Validator validator) {
        return new RequestRateLimiter(redisTemplate, redisScript, validator);
    }
}

