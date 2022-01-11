/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.mapper;

import cn.edu.sdu.qd.oj.user.entity.UserDO;
import cn.edu.sdu.qd.oj.user.entity.UserDOField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<UserDO> {

    /**
    * @Description 忽略逻辑删除字段，根据 username 查 userId
    **/
    @Select("SELECT " + UserDOField.ID + " FROM " + UserDOField.TABLE_NAME + " WHERE " + UserDOField.USERNAME + "=#{username} LIMIT 1")
    Long usernameToUserId(String username);

    /**
     * @Description 忽略逻辑删除字段，根据 userId 查 username
     **/
    @Select("SELECT " + UserDOField.USERNAME + " FROM " + UserDOField.TABLE_NAME + " WHERE " + UserDOField.ID + "=#{userId} LIMIT 1")
    String userIdToUsername(Long userId);

    /**
     * @Description 忽略逻辑删除字段，根据 userId 查 nickname
     **/
    @Select("SELECT " + UserDOField.NICKNAME + " FROM " + UserDOField.TABLE_NAME + " WHERE " + UserDOField.ID + "=#{userId} LIMIT 1")
    String userIdToNickname(Long userId);
}