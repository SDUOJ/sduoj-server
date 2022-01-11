/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.exception;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 自定义异常类
 * @author zhangt2333
 */

@Getter
@ToString
@AllArgsConstructor
public class ApiException extends RuntimeException {
    public int code;
    public String message;
    public Throwable exception;

    public ApiException(Throwable e, ApiExceptionEnum apiExceptionEnum) {
        this.exception = e;
        this.code = apiExceptionEnum.code;
        this.message = apiExceptionEnum.message;
    }

    public ApiException(ApiExceptionEnum apiExceptionEnum) {
        this.code = apiExceptionEnum.code;
        this.message = apiExceptionEnum.message;
    }

    public ApiException(ApiExceptionEnum apiExceptionEnum, String additionalMessage) {
        this.code = apiExceptionEnum.code;
        this.message = apiExceptionEnum.message + " " + additionalMessage;
    }
}