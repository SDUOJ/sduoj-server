/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.entity;

public class UserSessionDOField {
    public static final String TABLE_NAME = "oj_user_session";
    public static final String ID = "us_id";
    public static final String GMT_CREATE = "us_gmt_create";
    public static final String GMT_MODIFIED = "us_gmt_modified";
    public static final String FEATURES = "us_features";
    public static final String USERNAME = "u_username";
    public static final String IPV4 = "us_ipv4";
    public static final String USER_AGENT = "us_user_agent";
    public static final String SUCCESS = "us_is_success";
}