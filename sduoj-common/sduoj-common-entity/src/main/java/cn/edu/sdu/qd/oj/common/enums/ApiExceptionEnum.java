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
    PARAMETER_ERROR(400, "请求参数错误"),
    UNKNOWN_ERROR(500, "服务器出错"),
    GATEWAY_ERROR(500, "网关错误"),
    USER_NOT_FOUND(400, "该用户不存在"),
    PASSWORD_NOT_MATCHING(400, "该用户账号或密码错误"), // 不能直接提示密码错误哦
    PROBLEM_NOT_FOUND(400, "题目未找到"),
    PROBLEM_NOT_PUBLIC(403, "题目非公开"),
    SUBMISSION_NOT_FOUND(400, "提交未找到"),
    USER_NOT_MATCHING(403, "用户权限不足"),
    CONTENT_IS_BLANK(500, "文件内容为空"),
    FILE_WRITE_ERROR(500, "文件写入错误"),
    FILE_NOT_DOUBLE(400, "文件不配对"),
    FILE_NOT_EXISTS(400, "文件不存在"),
    FILE_TOO_LARGE(400, "文件过大，无法预览，请选择下载"),

    TOKEN_EXPIRE(400, "验证令牌过期或不存在"),
    ;

    public int code;
    public String message;
}
