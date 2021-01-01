/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.entity;

public class JudgeTemplateDOField {
    public static final String TABLE_NAME = "oj_judge_template";
    public static final String ID = "jt_id";
    public static final String GMT_CREATE = "jt_gmt_create";
    public static final String GMT_MODIFIED = "jt_gmt_modified";
    public static final String FEATURES = "jt_features";
    public static final String DELETED = "jt_is_deleted";
    public static final String VERSION = "jt_version";
    public static final String IS_PUBLIC = "jt_is_public";
    public static final String USER_ID = "u_id";
    public static final String TYPE = "jt_type";
    public static final String TITLE = "jt_title";
    public static final String SHELL_SCRIPT = "jt_shell_script";
    public static final String ZIP_FILE_ID = "f_id";
    public static final String ACCEPT_FILE_EXTENSIONS = "jt_accept_file_extensions";
    public static final String REMOTE_OJ = "jt_remote_oj";
    public static final String REMOTE_PARAMETERS = "jt_remote_parameters";
    public static final String COMMENT = "jt_comment";
    public static final String PROBLEM_IDS = "jt_problem_ids";
}