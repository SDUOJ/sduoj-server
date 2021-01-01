/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.List;
import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserSessionDTO extends BaseDTO {

    public static final String HEADER_KEY = "SDUOJUserInfo";
    public static final String HEADER_VALUE_LOGOUT = "logout";

    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String studentId;
    private Integer emailVerified;
    private List<String> roles;

    private String ipv4;
    private String userAgent;

    public boolean userIdEquals(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    public boolean userIdNotEquals(Long userId) {
        return !userIdEquals(userId);
    }
}