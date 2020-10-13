/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.util;

/**
 * @ClassName RedisConstant
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/5/3 18:03
 * @Version V1.0
 **/

public class RedisConstants {
    public static final String REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE = "hash_problemIdToTitle";
    public static final String REDIS_KEY_FOR_PROBLEM_ID_TO_PROBLEM_CODE = "hash_problemIdToProblemCode";
    public static final String REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM = "hash_problemIdToCheckpointNum";
    public static final String REDIS_KEY_FOR_PROBLEM_CODE_TO_PROBLEM_ID = "hash_problemCodeToProblemId";

    public static final String REDIS_KEY_FOR_USER_ID_TO_USERNAME = "hash_userIdToUsername";
    public static final String REDIS_KEY_FOR_USERNAME_TO_ID = "hash_usernameToUserId";

    public static final int CAPTCHA_EXPIRE = 60 * 5;
    public static final int ACPROBLEM_EXPIRE = 60 * 60 * 5;
    public static final int CONTEST_SUBMISSION_NUM_EXPIRE = 60 * 60 * 5;

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

}