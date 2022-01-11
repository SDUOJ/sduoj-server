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
import lombok.Getter;

/**
 * API异常枚举类
 * @author zhangt2333
 **/

@Getter
@AllArgsConstructor
public enum ApiExceptionEnum {
    PARAMETER_ERROR(400, "请求参数错误"),

    UNKNOWN_ERROR(500, "服务器出错"),
    INTERNAL_ERROR(500, "内部服务出错"),
    SERVER_BUSY(500, "服务器正忙，请重试"),

    GATEWAY_ERROR(500, "网关错误"),
    GET_IP_ERROR(500, "获取不到IP"),

    USER_NOT_FOUND(400, "该用户不存在"),
    USER_NOT_LOGIN(403, "该用户未登录"),


    PASSWORD_NOT_MATCHING(400, "该用户账号或密码错误"), // 不能直接提示密码错误哦
    NEWPASSWORD_LENGTH_ERROR(400, "新密码长度不在4~32位间"),

    PROBLEM_NOT_FOUND(400, "题目未找到"),
    PROBLEM_NOT_PUBLIC(403, "题目非公开"),
    DESCRIPTION_NOT_FOUND(400, "题面未找到"),

    JUDGETEMPLATE_NOT_FOUND(400, "评测模板未找到"),

    SUBMISSION_NOT_FOUND(400, "提交未找到"),
    SUBMISSION_PARAM_ERROR(400, "提交的代码和文件仅允许一个不空"),

    USER_NOT_MATCHING(403, "用户权限不足"),


    CONTENT_IS_BLANK(500, "文件内容为空"),
    FILE_WRITE_ERROR(500, "文件写入错误"),
    FILE_READ_ERROR(500, "文件读出错误"),
    FILE_NOT_DOUBLE(400, "文件不配对"),
    FILE_NOT_EXISTS(400, "文件不存在"),
    FILE_MD5_EXISTS(400, "文件md5已存在"),
    FILE_TOO_LARGE(400, "文件过大，无法预览，请选择下载"),
    FILE_NOT_MATCH(500, "文件md5相同但大小不相同，请联系管理员"),

    TOKEN_EXPIRE(400, "验证令牌过期或不存在"),

    CAPTCHA_NOT_FOUND(400, "验证码不存在"),
    CAPTCHA_NOT_MATCHING(400, "验证码不匹配"),

    TOO_FREQUENT(400, "操作太频繁，请稍后重试"),

    ENTITY_EXISTS(400, "创建失败, 相关信息已存在"),

    USER_EXIST(400, "用户名已存在或邮箱已被使用或对应第三方账号已存在"),

    FEATURE_ERROR(400, "特性字段限制"),


    THIRD_PARTY_NOT_EXIST(400, "第三方登录不存在"),
    THIRD_PARTY_ERROR(500, "第三方登录/创建/绑定出错"),
    THIRD_PARTY_BOUND(400, "第三方已绑定"),

    EMAIL_EXIST(400, "邮箱已存在"),
    NONE_EMAIL_SENDER(500, "服务器没有配置邮件发送者"),

    EMAIL_SEND_FAILED(500, "邮件发送失败，请联系管理员"),

    CONTEST_NOT_FOUND(400, "比赛不存在"),
    CONTEST_NOT_PARTICIPATE(403, "未参加这场比赛"),
    CONTEST_NOT_BEGIN(400, "比赛未开始"),
    CONTEST_HAD_PARTICIPATED(400, "请不要重复参加比赛"),
    CONTEST_PASSWORD_NOT_MATCHING(400, "比赛密码错误"),
    CONTEST_TIME_ERROR(400, "比赛时间必须满足: 当前时间<开始时间<结束时间"),

    ;

    public int code;
    public String message;
}