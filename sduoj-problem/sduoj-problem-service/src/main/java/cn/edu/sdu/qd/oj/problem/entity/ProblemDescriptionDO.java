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
@TableName(ProblemDescriptionDOField.TABLE_NAME)
public class ProblemDescriptionDO extends BaseDO {

    @TableId(value = ProblemDescriptionDOField.ID, type = IdType.AUTO)
    private Long id;

    @TableField(value = ProblemDescriptionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = ProblemDescriptionDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(ProblemDescriptionDOField.FEATURES)
    private String features;

    @TableField(ProblemDescriptionDOField.VERSION)
    @Version
    private Integer version;

    @TableField(ProblemDescriptionDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(ProblemDescriptionDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(ProblemDescriptionDOField.PROBLEM_ID)
    private Long problemId;

    @TableField(ProblemDescriptionDOField.USER_ID)
    private Long userId;

    @TableField(ProblemDescriptionDOField.VOTE_NUM)
    private Integer voteNum;

    @TableField(ProblemDescriptionDOField.TITLE)
    private String title;

    @TableField(ProblemDescriptionDOField.MARKDOWN_DESCRIPTION)
    private String markdownDescription;

    @TableField(ProblemDescriptionDOField.HTML_DESCRIPTION)
    private String htmlDescription;

    @TableField(ProblemDescriptionDOField.HTML_INPUT)
    private String htmlInput;

    @TableField(ProblemDescriptionDOField.HTML_OUTPUT)
    private String htmlOutput;

    @TableField(ProblemDescriptionDOField.HTML_SAMPLE_INPUT)
    private String htmlSampleInput;

    @TableField(ProblemDescriptionDOField.HTML_SAMPLE_OUTOUT)
    private String htmlSampleOutout;

    @TableField(ProblemDescriptionDOField.HTML_HINT)
    private String htmlHint;

    public static int compareById(ProblemDescriptionDO o1, ProblemDescriptionDO o2){
        return o1.id.compareTo(o2.id);
    }
}