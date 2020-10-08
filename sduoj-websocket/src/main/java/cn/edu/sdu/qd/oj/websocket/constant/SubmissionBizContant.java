/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.websocket.constant;

public class SubmissionBizContant {

    public static final String REDIS_CHANNEL_PATTERN = "/submission/*";

    public static final int REDIS_SUBMISSION_RESULT_EXPIRE = 30;

    public static String getRedisSubmissionKey(String submissionId) {
        return "submission:" + submissionId;
    }

    public static String getRedisChannelKey(String submissionId) {
        return "/submission/" + submissionId;
     }


}