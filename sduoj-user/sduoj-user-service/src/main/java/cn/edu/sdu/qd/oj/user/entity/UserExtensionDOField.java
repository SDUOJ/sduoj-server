/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.entity;

public class UserExtensionDOField {
    public static final String TABLE_NAME = "oj_user_extension";
    public static final String ID = "ue_id";
    public static final String GMT_CREATE = "ue_gmt_create";
    public static final String GMT_MODIFIED = "ue_gmt_modified";
    public static final String VERSION = "ue_version";
    public static final String DELETED = "ue_is_deleted";
    public static final String USER_ID = "u_id";
    public static final String KEY = "ue_key";
    public static final String VALUE = "ue_value";

    public static String acProblem(long contestId) {
        return "acproblem:" + contestId;
    }

    public static String participateContest() {
        return "pcontest";
    }

}