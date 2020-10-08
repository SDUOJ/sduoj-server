/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.entity;

public class ContestExtensionDOField {
    public static final String TABLE_NAME = "oj_contest_extension";
    public static final String ID = "ce_id";
    public static final String GMT_CREATE = "ce_gmt_create";
    public static final String GMT_MODIFIED = "ce_gmt_modified";
    public static final String DELETED = "ce_is_deleted";
    public static final String VERSION = "ce_version";
    public static final String CONTEST_ID = "ct_id";
    public static final String KEY = "ce_key";
    public static final String VALUE = "ce_value";
}