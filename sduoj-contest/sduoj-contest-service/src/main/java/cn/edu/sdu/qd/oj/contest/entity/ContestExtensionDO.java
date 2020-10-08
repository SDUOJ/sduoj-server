/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(ContestExtensionDOField.TABLE_NAME)
public class ContestExtensionDO extends BaseDO {

    @TableId(value = ContestExtensionDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = ContestExtensionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = ContestExtensionDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(ContestExtensionDOField.VERSION)
    @Version
    private Integer version;

    @TableField(ContestExtensionDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(ContestExtensionDOField.CONTEST_ID)
    private Long contestId;

    @TableField(ContestExtensionDOField.KEY)
    private String extensionKey;

    @TableField(ContestExtensionDOField.VALUE)
    private String extensionValue;
}