/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.auth.entity;

public class PermissionDOField {
    public static final String TABLE_NAME = "oj_url_permission";
    public static final String ID = "up_id";
    public static final String GMT_CREATE = "up_gmt_create";
    public static final String GMT_MODIFIED = "up_gmt_modified";
    public static final String FEATURES = "up_features";
    public static final String IS_DELETED = "up_is_deleted";
    public static final String VERSION = "up_version";
    public static final String URL = "up_url";
    public static final String NAME = "up_name";
    public static final String ROLES = "up_roles";
}