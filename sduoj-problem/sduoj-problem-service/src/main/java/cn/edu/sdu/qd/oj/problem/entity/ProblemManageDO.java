/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(ProblemDOField.TABLE_NAME)
public class ProblemManageDO extends BaseDO {

    @TableId(value = ProblemDOField.ID, type = IdType.AUTO)
    private Long problemId;

    @TableField(ProblemDOField.IS_PUBLIC)
    private Integer isPublic;

    @TableField(ProblemDOField.USER_ID)
    private Integer userId;

    @TableField(ProblemDOField.TITLE)
    private String problemTitle;

    @TableField(ProblemDOField.TIME_LIMIT)
    private Integer timeLimit;

    @TableField(ProblemDOField.MEMORY_LIMIT)
    private Integer memoryLimit;

    @TableField(ProblemDOField.CHECKPOINT_NUM)
    private Integer checkpointNum;

    @TableField(ProblemDOField.CHECKPOINTS)
    private byte[] checkpoints;

    @TableField(ProblemDOField.JUDGE_TEMPLATES)
    private String judgeTemplates;

    @TableField(exist = false)
    private String username;
}