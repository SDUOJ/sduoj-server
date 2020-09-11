/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.exception;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @ClassName OJException
 * @Description 自定义异常类
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Getter
@ToString
@AllArgsConstructor
public class ApiException extends RuntimeException {
    public int code;
    public String message;
    public ApiException(ApiExceptionEnum e) {
        this.code = e.code;
        this.message = e.message;
    }
}