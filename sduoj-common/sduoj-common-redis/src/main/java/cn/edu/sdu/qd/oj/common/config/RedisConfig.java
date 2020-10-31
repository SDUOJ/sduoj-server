/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.config;

import cn.edu.sdu.qd.oj.common.cache.AbstractCacheTypeManager;
import cn.edu.sdu.qd.oj.common.property.CacheTypeProperties;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.*;

@Configuration
@EnableCaching
@EnableRedisRepositories
@EnableConfigurationProperties({CacheTypeProperties.class})
public class RedisConfig {

    @Autowired
    private CacheTypeProperties cacheTypeProperties;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        Optional.ofNullable(redisPassword).filter(StringUtils::isNotEmpty).map(RedisPassword::of).ifPresent(configuration::setPassword);
        return new LettuceConnectionFactory(configuration);
    }


    @Bean
    public RedisSerializer<String> redisKeySerializer() {
        return RedisSerializer.string();
    }

    @Bean
    public RedisSerializer<Object> redisValueSerializer() {
        // 存Long取Int 的问题：Jackson 反序列化成 Object Long 泛型丢失
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        return jackson2JsonRedisSerializer;
        return new GenericFastJsonRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // key和hashKey采用String的序列化方式
        template.setKeySerializer(redisKeySerializer());
        template.setHashKeySerializer(redisKeySerializer());

        // value和hashValue序列化方式采用jackson
        template.setValueSerializer(redisValueSerializer());
        template.setHashValueSerializer(redisValueSerializer());

        template.afterPropertiesSet();
        return template;
    }

    private RedisCacheConfiguration getDefaultConf() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "cache".concat(":").concat(cacheName).concat(":"))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisKeySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisValueSerializer()))
                .entryTtl(Duration.ofHours(1))  ;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":").append(method.getName()).append(":");
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }


    @Bean
    @Primary
    public CacheManager cacheManager() {
        RedisCacheConfiguration defaultConf = getDefaultConf();

        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();

        // 硬编码的 cacheType
        applicationContext.getBeansOfType(AbstractCacheTypeManager.class).values().forEach(cacheTypeManager -> {
            cacheTypeManager.getCacheTypeList().forEach(cacheType -> {
                RedisCacheConfiguration conf = defaultConf.entryTtl(Duration.ofSeconds(cacheType.ttl));
                redisCacheConfigurationMap.put(cacheType.key, conf);
            });
        });
        // application.yml 配置中新定义、重写的 cacheType
        Optional.ofNullable(cacheTypeProperties.getCacheConfig()).ifPresent(o -> o.forEach((k, v) -> {
            RedisCacheConfiguration conf = defaultConf.entryTtl(Duration.ofSeconds(v));
            redisCacheConfigurationMap.put(k, conf);
        }));

        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(defaultConf)
                .withInitialCacheConfigurations(redisCacheConfigurationMap)
                .build();
    }
}
