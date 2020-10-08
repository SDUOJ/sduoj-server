/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(SubmissionDOField.TABLE_NAME)
public class SubmissionListDO extends BaseDO {

    @TableId(value = SubmissionDOField.ID)
    private Long submissionId;

    @TableField(value = SubmissionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = SubmissionDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(SubmissionDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(SubmissionDOField.VALID)
    private Integer valid;

    @TableField(SubmissionDOField.PROBLEM_ID)
    private Long problemId;

    @TableField(SubmissionDOField.USER_ID)
    private Long userId;

    @TableField(SubmissionDOField.CONTEST_ID)
    private Long contestId;

    @TableField(SubmissionDOField.LANGUAGE)
    private String language;

    @TableField(SubmissionDOField.JUDGE_RESULT)
    private Integer judgeResult;

    @TableField(SubmissionDOField.JUDGE_SCORE)
    private Integer judgeScore;

    @TableField(SubmissionDOField.USED_TIME)
    private Integer usedTime;

    @TableField(SubmissionDOField.USED_MEMORY)
    private Integer usedMemory;

    @TableField(SubmissionDOField.CODE_LENGTH)
    private Integer codeLength;
}