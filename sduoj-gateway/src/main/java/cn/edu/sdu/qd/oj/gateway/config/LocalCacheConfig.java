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

import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableCaching
@Configuration
public class LocalCacheConfig {

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = Lists.newArrayList(
                new CaffeineCache(RedisConstants.URL_TO_ROLES, Caffeine.newBuilder().recordStats().expireAfterWrite(60, TimeUnit.SECONDS).build()),
                new CaffeineCache(RedisConstants.USER_ID_TO_ROLES, Caffeine.newBuilder().recordStats().expireAfterWrite(10, TimeUnit.SECONDS).build())
        );
        cacheManager.setCaches(caches);
        return cacheManager;
    }

}