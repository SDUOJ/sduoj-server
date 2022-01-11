/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.tag.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(TagDOField.TABLE_NAME)
public class TagDO extends BaseDO {

    @TableId(value = TagDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = TagDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = TagDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(TagDOField.FEATURES)
    private String features;

    @TableField(TagDOField.IS_DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(TagDOField.PARENT_ID)
    private Long parentId;

    @TableField(TagDOField.TITLE)
    private String title;
}