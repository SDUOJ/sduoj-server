/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.exception;

import cn.edu.sdu.qd.oj.common.enums.ExceptionEnum;
import lombok.Getter;

/**
 * @ClassName OJException
 * @Description 自定义异常类
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Getter
public class OJException extends RuntimeException {

    private ExceptionEnum exceptionEnum;

    public OJException(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }
}
