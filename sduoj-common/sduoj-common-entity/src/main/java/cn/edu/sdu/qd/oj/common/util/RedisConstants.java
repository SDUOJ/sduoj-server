/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.util;

/**
 * some constants for redis
 * @author zhangt2333
 */

public class RedisConstants {

    public static final String ALL_URL_PERMISSION = "allUrlPermission";
    public static final String URL_TO_ROLES_MAP = "urlToRolesMap";
    public static final String URL_TO_ROLES = "urlToRoles";

    public static final String USER_ID_TO_USERNAME = "userIdToUsername";
    public static final String USER_ID_TO_NICKNAME = "userIdToNickname";
    public static final String USERNAME_TO_USERID = "usernameToUserId";
    public static final String USER_ID_TO_ROLES = "userIdToRoles";

    public static final String PROBLEM_ID_TO_PROBLEM_TITLE = "problemIdToProblemTitle";
    public static final String PROBLEM_ID_TO_PROBLEM_CHECKPOINT_NUM = "problemIdToProblemCheckpointNum";
    public static final String PROBLEM_CODE_TO_PROBLEM_ID = "problemCodeToProblemId";
    public static final String PROBLEM_ID_TO_PROBLEM_CODE = "problemIdToProblemCode";

    public static final String JUDGE_TEMPLATE_ID_TO_TITLE = "judgeTemplateIdToTitle";
    public static final String JUDGE_TEMPLATE_ID_TO_TYPE = "judgeTemplateIdToType";

    public static final int CAPTCHA_EXPIRE = 60 * 5;
    public static final int ACPROBLEM_EXPIRE = 60 * 60 * 5;
    public static final int CONTEST_SUBMISSION_NUM_EXPIRE = 60 * 60 * 5;

    public static final int SDU_CAS_EXPIRE = 60 * 60;

    // ------------- 验证码 ----------------------
    public static String getCaptchaKey(String uuid) {
        return "captch:" + uuid;
    }

    // ------------- 忘记密码token ----------------------
    public static String getForgetPasswordKey(String uuid) {
        return "forgetPassword:" + uuid;
    }

    // ------------- 邮箱验证token ----------------------
    public static String getEmailVerificationKey(String uuid) {
        return "emailVerification:" + uuid;
    }

    public static String getContestRank(long contestId) {
        return "contestRank:" + contestId;
    }

    // ------------- 用户在某场比赛的过题 ----------------------
    public static String getUserACProblem(long contestId, long userId) {
        return "acproblem:" + contestId + ":" + userId;
    }

    // ------------- 比赛中的过题 ----------------------
    public static String getContestSubmission(long contestId) {
        return "contestSubmission:" + contestId;
    }

    public static String getContestProblemAccept(String problemCode) {
        return "ac:" + problemCode;
    }

    public static String getContestProblemSubmit(String problemCode) {
        return "su:" + problemCode;
    }

    // ------------- 第三方认证相关 ----------------------
    public static String getThirdPartyToken(String uuid) {
        return "ThirdParty:" + uuid;
    }
}