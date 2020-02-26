/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.enums;

import lombok.AllArgsConstructor;

/**
 * @ClassName ExceptionEnum
 * @Description API异常枚举类，需要客户端处理
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@AllArgsConstructor
public enum ExceptionEnum {
    USER_NOT_FOUND(400, "该用户不存在"),
    ;

    public int code;
    public String message;
}
