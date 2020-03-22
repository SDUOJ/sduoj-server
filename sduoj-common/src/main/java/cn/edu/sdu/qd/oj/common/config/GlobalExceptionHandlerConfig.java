/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.config;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
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
        log.info(e.toString());
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                .body(ResponseResult.fail(HttpStatus.FORBIDDEN));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult> handleException(Exception e) {
        log.info(e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(InternalApiException.class)
    public ResponseEntity<ResponseResult> handleException(InternalApiException e) {
        log.info(e.toString());
        return ResponseEntity.status(e.code)
                .body(ResponseResult.fail(e.code, e.message));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseResult> handleException(ApiException e) {
        log.info(e.toString());
        return ResponseEntity.status(e.code)
                .body(ResponseResult.fail(e.code, e.message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseResult> exception(MethodArgumentNotValidException e) {
        log.info(e.toString());
        String message = e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(";"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), message));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseResult> handleException(HttpMessageNotReadableException e) {
        log.info(e.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(ResponseResult.fail(HttpStatus.BAD_REQUEST));
    }
}
