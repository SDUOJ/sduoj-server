/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.filesys.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(FileDOField.TABLE_NAME)
public class FileDO extends BaseDO {

    @TableId(value = FileDOField.ID, type = IdType.NONE)
    private Long id;

    @TableField(value = FileDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = FileDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(FileDOField.FEATURES)
    private String features;

    @TableField(FileDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(FileDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(FileDOField.USER_ID)
    private Long userId;

    @TableField(FileDOField.SIZE)
    private Long size;

    @TableField(FileDOField.NAME)
    private String name;

    @TableField(FileDOField.EXTENSION_NAME)
    private String extensionName;

    @TableField(FileDOField.MD5)
    private String md5;
}