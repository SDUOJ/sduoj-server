/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SubmissionJudgeResult {

    PD(0,"Pending"),
    AC(1,"Accepted"),
    TLE(2,"Time Limit Exceeded"),
    MLE(3,"Memory Limit Exceeded"),
    RE(4,"Runtime Error"),
    SE(5,"System Error"),
    WA(6,"Wrong Answer"),
    PR(7,"Presentation Error"),
    CE(8,"Compile Error"),


    ;

    public int code;
    public String message;

    public boolean equals(Integer code) {
        if (code == null) {
            return false;
        }
        return this.code == code;
    }

    public static SubmissionJudgeResult of(Integer code) {
        for (SubmissionJudgeResult one : SubmissionJudgeResult.values()) {
            if (one.equals(code)) {
                return one;
            }
        }
        return null;
    }
}
