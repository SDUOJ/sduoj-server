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

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.util.Date;

/**
 * @ClassName UserDO
 * @Author zhangt2333
 * @Date 2020/9/7 16:54
 * @Version V1.0
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(UserDOField.TABLE_NAME)
public class UserDO extends BaseDO {

    @TableId(value = UserDOField.ID, type = IdType.AUTO)
    private Long userId;

    @TableField(value = UserDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = UserDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(UserDOField.FEATURES)
    private String features;

    @TableField(UserDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(UserDOField.VERSION)
    @Version
    private Integer version;

    @TableField(UserDOField.USERNAME)
    private String username;

    @TableField(UserDOField.NICKNAME)
    private String nickname;

    @TableField(UserDOField.SALT)
    private String salt;

    @TableField(UserDOField.PASSWORD)
    private String password;

    @TableField(UserDOField.EMAIL)
    private String email;

    @TableField(UserDOField.PHONE)
    private String phone;

    @TableField(UserDOField.GENDER)
    private Integer gender;

    @TableField(UserDOField.STUDENT_ID)
    private String studentId;

    @TableField(UserDOField.ROLES)
    private String roles;

    @TableField(UserDOField.SDU_ID)
    private String sduId;
}