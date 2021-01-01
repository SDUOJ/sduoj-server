/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(JudgeTemplateDOField.TABLE_NAME)
public class JudgeTemplateManageListDO extends BaseDO {

    @TableId(value = JudgeTemplateDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = JudgeTemplateDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = JudgeTemplateDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(JudgeTemplateDOField.FEATURES)
    private String features;

    @TableField(JudgeTemplateDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(JudgeTemplateDOField.VERSION)
    @Version
    private Integer version;

    @TableField(JudgeTemplateDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(JudgeTemplateDOField.USER_ID)
    private Long userId;

    @TableField(JudgeTemplateDOField.TYPE)
    private Integer type;

    @TableField(JudgeTemplateDOField.TITLE)
    private String title;

    @TableField(JudgeTemplateDOField.ZIP_FILE_ID)
    private Long zipFileId;

    @TableField(JudgeTemplateDOField.ACCEPT_FILE_EXTENSIONS)
    private String acceptFileExtensions;

    @TableField(JudgeTemplateDOField.REMOTE_OJ)
    private String remoteOj;

    @TableField(JudgeTemplateDOField.COMMENT)
    private String comment;
}