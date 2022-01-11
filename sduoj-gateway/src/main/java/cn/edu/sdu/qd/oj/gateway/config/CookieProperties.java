/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.gateway.config;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@EqualsAndHashCode
@ConfigurationProperties(prefix = "sduoj.cookie")
public class CookieProperties {

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration maxAge;

    public void setMaxAge(Duration maxAge) {
        this.maxAge = maxAge;
        // TODO: 更好的 event 驱动模式来监听配置值的变化，使配置值的发布和监听解耦
    }
}
