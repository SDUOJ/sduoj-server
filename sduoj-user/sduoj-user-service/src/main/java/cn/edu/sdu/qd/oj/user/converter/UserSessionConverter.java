/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.user.entity.UserDO;
import cn.edu.sdu.qd.oj.user.entity.UserSessionDO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserSessionConverter extends BaseConverter<UserSessionDO, UserSessionDTO> {

    default UserSessionDTO to(UserDO userDO, UserSessionDO userSessionDO) {
        return UserSessionDTO.builder()
                .userId(userDO.getUserId())
                .username(userDO.getUsername())
                .nickname(userDO.getNickname())
                .email(userDO.getEmail())
                .roles(stringToList(userDO.getRoles()))
                .studentId(userDO.getStudentId())
                .emailVerified(userDO.getEmailVerified())
                .ipv4(userSessionDO.getIpv4())
                .userAgent(userSessionDO.getUserAgent())
                .build();
    }

}