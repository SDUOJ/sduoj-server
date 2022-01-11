/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.auth.enums;

import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@AllArgsConstructor
public enum PermissionEnum {

    ALL("all"), // URL 专用

    SUPERADMIN("superadmin"),
    ADMIN("admin"),
    USER("user"),

    ;

    public String name;

    public boolean notIn(UserSessionDTO userSessionDTO) {
        return !in(userSessionDTO);
    }

    public boolean in(UserSessionDTO userSessionDTO) {
        return userSessionDTO != null && in(userSessionDTO.getRoles());
    }

    public boolean in(List<String> roles) {
        // the size of roles is usually small
        if (roles == null) {
            return false;
        }
        for (String role : roles) {
            if (role.equalsIgnoreCase(this.name)) {
                return true;
            }
        }
        return false;
    }

    public boolean in(String roles) {
        if (StringUtils.isBlank(roles)) {
            return false;
        }
        return roles.contains(this.name);
    }
}
