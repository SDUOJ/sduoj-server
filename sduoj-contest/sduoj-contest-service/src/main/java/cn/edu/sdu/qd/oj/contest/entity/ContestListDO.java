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
import cn.edu.sdu.qd.oj.contest.converter.ContestConvertUtils;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(ContestDOField.TABLE_NAME)
public class ContestListDO extends BaseDO {

    @TableId(value = ContestDOField.ID, type = IdType.AUTO)
    private Long contestId;

    @TableField(value = ContestDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = ContestDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(ContestDOField.FEATURES)
    private String features;

    @TableField(ContestDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(ContestDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(ContestDOField.TITLE)
    private String contestTitle;

    @TableField(ContestDOField.USER_ID)
    private Long userId;

    @TableField(ContestDOField.GMT_START)
    private Date gmtStart;

    @TableField(ContestDOField.GMT_END)
    private Date gmtEnd;

    @TableField(ContestDOField.SOURCE)
    private String source;

    @TableField(ContestDOField.PARTICIPANT_NUM)
    private Integer participantNum;
}