/*
 * Copyright 2020-2021 the original author or authors.
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
public class SubmissionJudgeDO extends BaseDO {


    @TableId(value = SubmissionDOField.ID)
    private Long submissionId;

    @TableField(SubmissionDOField.PROBLEM_ID)
    private Long problemId;

    @TableField(SubmissionDOField.USER_ID)
    private Long userId;

    @TableField(SubmissionDOField.JUDGE_TEMPLATE_ID)
    private Long judgeTemplateId;

    @TableField(SubmissionDOField.ZIP_FILE_ID)
    private Long zipFileId;

    @TableField(value = SubmissionDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(SubmissionDOField.CODE)
    private String code;

    @TableField(SubmissionDOField.CODE_LENGTH)
    private String codeLength;
}