/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.advice;

import cn.edu.sdu.qd.oj.common.exception.OJException;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
public class BasicExceptionHandler {
    @ExceptionHandler(OJException.class)
    public ResponseEntity<ResponseResult> handleException(OJException e) {
        return ResponseEntity.status(e.getExceptionEnum().code)
                .body(ResponseResult.error(e.getExceptionEnum()));
    }
}
