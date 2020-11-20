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
public enum ContestOpennessEnum {

    PUBLIC("public"),
    PROTECTED("protected"),
    PRIVATE("private"),
    ;

    public String name;

    public boolean equals(String name) {
        return this.name.equals(name);
    }

    public static ContestOpennessEnum of(String type) {
        for (ContestOpennessEnum value : ContestOpennessEnum.values()) {
            if (value.name.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }
}
