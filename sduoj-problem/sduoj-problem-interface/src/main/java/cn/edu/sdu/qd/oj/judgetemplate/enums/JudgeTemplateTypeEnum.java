/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum JudgeTemplateTypeEnum {

    IO(0),
    SPJ(1),
    ADVANCED(2),
    ;

    public int code;

    public static JudgeTemplateTypeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (JudgeTemplateTypeEnum value : JudgeTemplateTypeEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
