/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public class NonExceptionOptional<T> {
    public static <T> Optional<T> ofNullable(Supplier<? extends T> supplier) {
        return ofNullable(true, supplier);
    }
    public static <T> Optional<T> ofNullable(boolean logPrint, Supplier<? extends T> supplier) {
        T t = null;
        try {
            t = supplier.get();
        } catch (Throwable e) {
            if (logPrint) {
                log.warn("", e);
            }
        }
        return Optional.ofNullable(t);
    }
}
