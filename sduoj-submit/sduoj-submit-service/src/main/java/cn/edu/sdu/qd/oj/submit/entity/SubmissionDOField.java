/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.entity;

public class SubmissionDOField {
    public static final String TABLE_NAME = "oj_submission";
    public static final String ID = "s_id";
    public static final String GMT_CREATE = "s_gmt_create";
    public static final String GMT_MODIFIED = "s_gmt_modified";
    public static final String FEATURES = "s_features";
    public static final String VERSION = "s_version";
    public static final String IS_PUBLIC = "s_is_public";
    public static final String VALID = "s_valid";
    public static final String PROBLEM_ID = "p_id";
    public static final String USER_ID = "u_id";
    public static final String CONTEST_ID = "ct_id";
    public static final String JUDGE_TEMPLATE_ID = "jt_id";
    public static final String ZIP_FILE_ID = "f_id";
    public static final String IPV4 = "s_ipv4";
    public static final String JUDGER_ID = "s_judger_id";
    public static final String JUDGE_RESULT = "s_judge_result";
    public static final String JUDGE_SCORE = "s_judge_score";
    public static final String USED_TIME = "s_used_time";
    public static final String USED_MEMORY = "s_used_memory";
    public static final String CODE_LENGTH = "s_code_length";
    public static final String JUDGE_LOG = "s_judge_log";
    public static final String CODE = "s_code";
    public static final String CHECKPOINT_RESULTS = "s_checkpoint_results";
}