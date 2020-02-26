/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.enums;

import lombok.AllArgsConstructor;

/**
 * @ClassName AcceptedEnum
 * @Description API正确反馈枚举类，异常则属于服务端处理
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@AllArgsConstructor
public enum AcceptedEnum {
    OK(1, "成功"),
    ERROR(0, "失败")  // 需要服务端处理的异常
    ;

    public int code;
    public String message;
}
