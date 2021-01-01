/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(ProblemExtensionDOField.TABLE_NAME)
public class ProblemExtensionDO extends BaseDO {

    @TableId(value = ProblemExtensionDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = ProblemExtensionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = ProblemExtensionDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(ProblemExtensionDOField.VERSION)
    @Version
    private Integer version;

    @TableField(ProblemExtensionDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(ProblemExtensionDOField.PROBLEM_ID)
    private Long problemId;

    @TableField(ProblemExtensionDOField.KEY)
    private String extensionKey;

    @TableField(ProblemExtensionDOField.VALUE)
    private String extensionValue;
}