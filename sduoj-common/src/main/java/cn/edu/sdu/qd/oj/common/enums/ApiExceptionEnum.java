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
public enum ApiExceptionEnum {
    UNKNOWN_ERROR(500, "未知错误"),
    GATEWAY_ERROR(500, "网关错误"),
    USER_NOT_FOUND(500, "该用户不存在"),
    PASSWORD_NOT_MATCHING(500, "该用户账号或密码错误"), // 不能直接提示密码错误哦
    PROBLEM_NOT_FOUND(500, "题目未找到"),
    PROBLEM_NOT_PUBLIC(403, "题目非公开"),
    SUBMISSION_NOT_FOUND(500, "提交未找到"),
    USER_NOT_MATCHING(403, "用户权限不足"),
    CONTENT_IS_BLANK(500, "文件内容为空"),
    FILE_WRITE_ERROR(500, "文件写入错误"),
    FILE_NOT_DOUBLE(500, "文件不配对"),
    ;

    public int code;
    public String message;
}
