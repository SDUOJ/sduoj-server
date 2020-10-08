/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;

/**
 * @Author zhangt2333
 * @Date 2020/9/8 10:30
 * @Version V1.0
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(CheckpointDOField.TABLE_NAME)
public class CheckpointDO extends BaseDO {
    public static final int MAX_DESCRIPTIONs_LENGTH = 32;

    @TableId(value = CheckpointDOField.ID, type = IdType.NONE)
    private Long checkpointId;

    @TableField(value = CheckpointDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = CheckpointDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(CheckpointDOField.INPUT_DESC)
    private String inputDescription;

    @TableField(CheckpointDOField.OUTPUT_DESC)
    private String outputDescription;

    @TableField(CheckpointDOField.INPUT_SIZE)
    private Integer inputSize;

    @TableField(CheckpointDOField.OUTPUT_SIZE)
    private Integer outputSize;

    @TableField(CheckpointDOField.INPUT_FILE_NAME)
    private String inputFileName;

    @TableField(CheckpointDOField.OUTPUT_FILE_NAME)
    private String outputFileName;

    public CheckpointDO(Long checkpointId, String inputDescription, String outputDescription, Integer inputSize, Integer outputSize) {
        this.checkpointId = checkpointId;
        this.inputDescription = inputDescription;
        this.outputDescription = outputDescription;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
    }
}