/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ContestTypeEnum {

    OI("OI"),
    IOI("IOI"),
    ACM("ACM"),
    ;

    public String type;

    public static ContestTypeEnum of(String type) {
        for (ContestTypeEnum value : ContestTypeEnum.values()) {
            if (value.type.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }
}
