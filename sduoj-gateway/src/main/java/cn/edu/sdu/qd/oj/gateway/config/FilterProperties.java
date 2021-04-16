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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * 过滤器配置类
 * @author zhangt2333
 **/

@ConfigurationProperties(prefix = "sduoj.filter")
@Getter
@Setter
public class FilterProperties {
    private List<String> allowPaths;

    private Map<String, Integer> globalLimitPaths;

    private Map<String, Integer> userLimitPaths;
}