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