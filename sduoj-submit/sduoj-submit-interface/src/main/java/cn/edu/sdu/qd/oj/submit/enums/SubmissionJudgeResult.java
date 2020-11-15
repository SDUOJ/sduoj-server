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

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum SubmissionJudgeResult {
    // 状态码
    COMPILING(-3, "Compiling"),
    JUDGING(-2, "Judging"),
    END(-1, "End"),
    PD(0,"Pending"),

    // 结果码
    AC(1,"Accepted"),
    TLE(2,"Time Limit Exceeded"),
    MLE(3,"Memory Limit Exceeded"),
    RE(4,"Runtime Error"),
    SE(5,"System Error"),
    WA(6,"Wrong Answer"),
    PR(7,"Presentation Error"),
    CE(8,"Compilation Error"),


    ;

    public int code;
    public String message;

    public static final List<Integer> CAN_REJUDGE_RESULT_CODE = Arrays.asList(
            PD.code,
            AC.code,
            TLE.code,
            MLE.code,
            RE.code,
            SE.code,
            WA.code,
            PR.code,
            CE.code
    );

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
