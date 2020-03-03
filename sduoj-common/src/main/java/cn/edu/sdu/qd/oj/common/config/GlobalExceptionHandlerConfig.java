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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                .body(ResponseResult.fail(HttpStatus.FORBIDDEN));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @ExceptionHandler(InternalApiException.class)
    public ResponseEntity<ResponseResult> handleException(InternalApiException e) {
        return ResponseEntity.status(e.code)
                .body(ResponseResult.fail(e.code, e.message));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseResult> handleException(ApiException e) {
        return ResponseEntity.status(e.code)
                .body(ResponseResult.fail(e.code, e.message));
    }

}
