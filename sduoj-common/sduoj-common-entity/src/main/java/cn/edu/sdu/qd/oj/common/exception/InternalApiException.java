/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.exception;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @ClassName InternalApiException
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/3 11:40
 * @Version V1.0
 **/

@Getter
@ToString
@AllArgsConstructor
public class InternalApiException extends Exception {
    public int code;
    public String message;
    public InternalApiException(ApiExceptionEnum e) {
        this.code = e.code;
        this.message = e.message;
    }
}