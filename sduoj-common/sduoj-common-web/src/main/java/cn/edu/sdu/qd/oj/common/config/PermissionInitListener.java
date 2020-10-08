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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import cn.edu.sdu.qd.oj.auth.api.PermissionApi;
import cn.edu.sdu.qd.oj.auth.dto.PermissionDTO;
import feign.Feign;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
public class PermissionInitListener implements SpringApplicationRunListener {

    public PermissionInitListener(SpringApplication application, String[] args) {
        super();
    }

    private static final Class<? extends Annotation>[] MAPPING_CLASSES = new Class[] {RequestMapping.class,
            GetMapping.class, PostMapping.class, PutMapping.class, DeleteMapping.class};

    private static final Set<String> ESCAPE_CONTROLLER = new HashSet<>(Arrays.asList("basicErrorController"));

    private LoadBalancerClient loadBalancerClient;

    @Override
    public void started(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        String applicationPrefixUrl = Optional.ofNullable(environment.getProperty("server.servlet.context-path"))
                .filter(StringUtils::isNotBlank)
                .orElse("");

        List<PermissionDTO> permissionDTOList = Lists.newArrayList();

        Map<String, Object> beanMap = context.getBeansWithAnnotation(Controller.class);
        for (Entry<String, Object> objectEntry : beanMap.entrySet()) {
            if (ESCAPE_CONTROLLER.contains(objectEntry.getKey())) {
                continue;
            }

            Object value = objectEntry.getValue();
            Class<?> valueClass = value.getClass();

            String classMappingUrl = Optional.ofNullable(valueClass.getAnnotation(RequestMapping.class))
                    .map(RequestMapping::value)
                    .filter(ArrayUtils::isNotEmpty)
                    .map(values -> values[0])
                    .orElse("");

            Method[] methods = valueClass.getMethods();
            for (Method method : methods) {
                for (Class<? extends Annotation> mappingClass : MAPPING_CLASSES) {
                    Annotation mappingAnnotation = method.getAnnotation(mappingClass);
                    if (mappingAnnotation != null) {
                        try {
                            Method valueMethod = mappingClass.getDeclaredMethod("value");
                            Method nameMethod = mappingClass.getDeclaredMethod("name");
                            String methodMappingUrl = Optional.ofNullable((String[]) valueMethod.invoke(mappingAnnotation))
                                    .filter(ArrayUtils::isNotEmpty)
                                    .map(values -> values[0])
                                    .orElse("");
                            String url = String.format("/%s/%s/%s", applicationPrefixUrl, classMappingUrl, methodMappingUrl)
                                    .replaceAll("[/]+", "/");
                            String name = (String) nameMethod.invoke(mappingAnnotation);
                            permissionDTOList.add(PermissionDTO.builder()
                                    .name(name)
                                    .url(url)
                                    .build());
                            log.info("syncUrl {} {}", name, url);
                        } catch (Throwable t) {
                            log.error("", t);
                        }
                    }
                }
            }
        }

        // 远程调用 auth 微服务同步过去
        if (!permissionDTOList.isEmpty()) {
            try {
                loadBalancerClient = (LoadBalancerClient) context.getBean("loadBalancerClient");
            } catch (Throwable t) {
                log.error("", t);
            }
            syncUrl(permissionDTOList, 0);
        }
    }

    private void syncUrl(List<PermissionDTO> permissionDTOList, int tryTime) {
        // 失败则 10 秒后重试一次
        try {
            Feign.builder()
                    .contract(new SpringMvcContract())
                    .encoder(new JacksonEncoder())
                    .target(PermissionApi.class, loadBalancerClient.choose(PermissionApi.SERVICE_NAME).getUri().toString())
                    .sync(permissionDTOList);
        } catch (Throwable t) {
            log.error("", t);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    syncUrl(permissionDTOList, tryTime + 1);
                }
            }, 10000 + tryTime * 5000);
        }
    }

    @Override
    public void starting() {

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {

    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }
}