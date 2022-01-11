/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.entity;

public class ProblemDOField {
    public static final String TABLE_NAME = "oj_problem";
    public static final String ID = "p_id";
    public static final String GMT_CREATE = "p_gmt_create";
    public static final String GMT_MODIFIED = "p_gmt_modified";
    public static final String FEATURES = "p_features";
    public static final String DELETED = "p_is_deleted";
    public static final String VERSION = "p_version";
    public static final String CODE = "p_code";
    public static final String IS_PUBLIC = "p_is_public";
    public static final String USER_ID = "u_id";
    public static final String TITLE = "p_title";
    public static final String SOURCE = "p_source";
    public static final String REMOTE_OJ = "p_remote_oj";
    public static final String REMOTE_URL = "p_remote_url";
    public static final String SUBMIT_NUM = "p_submit_num";
    public static final String ACCEPT_NUM = "p_accept_num";
    public static final String MEMORY_LIMIT = "p_memory_limit";
    public static final String TIME_LIMIT = "p_time_limit";
    public static final String OUTPUT_LIMIT = "p_output_limit";
    public static final String DEFAULT_DESCRIPTION_ID = "p_default_pd_id";
    public static final String CHECKPOINT_NUM = "p_checkpoint_num";
    public static final String CHECKPOINTS = "p_checkpoints";
    public static final String JUDGE_TEMPLATES = "p_judge_templates";
    public static final String CHECKPOINT_CASES = "p_checkpoint_cases";
    public static final String CHECKER_CONFIG = "p_checker_config";
    public static final String FUNCTION_TEMPLATES = "p_function_templates";
}