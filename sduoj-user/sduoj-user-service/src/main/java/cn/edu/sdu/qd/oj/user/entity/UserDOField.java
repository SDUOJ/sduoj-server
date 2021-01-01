/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.entity;

/**
 * @ClassName UserDOField
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/9/7 16:54
 * @Version V1.0
 **/

public class UserDOField {
    public static final String TABLE_NAME = "oj_user";
    public static final String ID = "u_id";
    public static final String GMT_CREATE = "u_gmt_create";
    public static final String GMT_MODIFIED = "u_gmt_modified";
    public static final String FEATURES = "u_features";
    public static final String DELETED = "u_is_deleted";
    public static final String VERSION = "u_version";
    public static final String USERNAME = "u_username";
    public static final String NICKNAME = "u_nickname";
    public static final String SALT = "u_salt";
    public static final String PASSWORD = "u_password";
    public static final String EMAIL = "u_email";
    public static final String EMAIL_VERIFIED = "u_is_email_verified";
    public static final String PHONE = "u_phone";
    public static final String GENDER = "u_gender";
    public static final String STUDENT_ID = "u_student_id";
    public static final String ROLES = "u_roles";
}