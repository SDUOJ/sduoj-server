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
}
