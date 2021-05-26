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

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

public class AssertUtils {

    /**
     * @param message 必须符合 "{0} {1}" 即 {ArgumentIndex} 的格式
     */
    public static ApiException newException(ApiExceptionEnum exceptionEnum, String message, Object... args) {
        if (StringUtils.isNotBlank(message)) {
            return new ApiException(exceptionEnum, MessageFormat.format(message, args));
        }
        return new ApiException(exceptionEnum);
    }

    public static void isTrue(boolean expression, ApiExceptionEnum exceptionEnum) {
        isTrue(expression, exceptionEnum, null);
    }

    /**
     * @param message 必须符合 "{0} {1}" 即 {ArgumentIndex} 的格式
     */
    public static void isTrue(boolean expression, ApiExceptionEnum exceptionEnum, String message, Object... args) {
        if (!expression) {
            throw newException(exceptionEnum, message, args);
        }
    }

    public static void notNull(Object obj, ApiExceptionEnum exceptionEnum) {
        notNull(obj, exceptionEnum, null);
    }

    /**
     * @param message 必须符合 "{0} {1}" 即 {ArgumentIndex} 的格式
     */
    public static void notNull(Object obj, ApiExceptionEnum exceptionEnum, String message, Object... args) {
        if (obj == null) {
            throw newException(exceptionEnum, message, args);
        }
    }

}
