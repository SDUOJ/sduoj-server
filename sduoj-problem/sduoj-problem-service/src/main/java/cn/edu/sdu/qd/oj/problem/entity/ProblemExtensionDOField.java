/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.entity;

public class ProblemExtensionDOField {
    public static final String TABLE_NAME = "oj_problem_extension";
    public static final String ID = "pe_id";
    public static final String GMT_CREATE = "pe_gmt_create";
    public static final String GMT_MODIFIED = "pe_gmt_modified";
    public static final String VERSION = "pe_is_deleted";
    public static final String DELETED = "pe_version";
    public static final String PROBLEM_ID = "p_id";
    public static final String KEY = "pe_key";
    public static final String VALUE = "pe_value";

    public static String problemCase() {
        return "problemCase";
    }
}