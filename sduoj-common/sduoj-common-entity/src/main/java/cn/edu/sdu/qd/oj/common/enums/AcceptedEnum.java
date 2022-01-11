/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

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
    OK(0, "成功"),
    ERROR(1, "失败")  // 需要服务端处理的异常
    ;

    public int code;
    public String message;
}
