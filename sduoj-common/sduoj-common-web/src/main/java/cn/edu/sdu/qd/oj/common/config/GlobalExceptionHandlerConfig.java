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

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

/**
 * @ClassName BasicExceptionHandler
 * @Description 通用异常拦截器
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlerConfig {

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseResult> handleException(MissingServletRequestParameterException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                .body(ResponseResult.fail(HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult> handleException(Exception e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(InternalApiException.class)
    public ResponseEntity<ResponseResult> handleException(InternalApiException e) {
        log.warn("", e);
        return ResponseEntity.status(e.code)
                .body(ResponseResult.fail(e.code, e.message));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseResult> handleException(ApiException e) {
        log.warn("{} {} {}", e.getCode(), e.getMessage(), e.getStackTrace()[0]);
        return ResponseEntity.status(e.code)
                .body(ResponseResult.fail(e.code, e.message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseResult> exception(MethodArgumentNotValidException e) {
        log.warn("{} {}", e.getMessage(), e.getStackTrace()[0]);
        String message = e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(";"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), message));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleException(HttpMessageNotReadableException e) {
        log.warn("{} {}", e.getMessage(), e.getStackTrace()[0]);
        throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
    }
}