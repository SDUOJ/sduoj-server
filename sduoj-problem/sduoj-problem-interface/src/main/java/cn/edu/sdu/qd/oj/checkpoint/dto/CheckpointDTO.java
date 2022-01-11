/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.Date;


/**
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:23
 * @Version V1.0
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CheckpointDTO extends BaseDTO {

    public static final int MAX_DESCRIPTION_LENGTH = 64;

    private Long checkpointId;

    private Date gmtCreate;

    private Date gmtModified;

    private String inputPreview;

    private String outputPreview;

    private Integer inputSize;

    private Integer outputSize;

    private String inputFilename;

    private String outputFilename;

    private String input;

    private String output;

    private Long inputFileId;

    private Long outputFileId;

    public CheckpointDTO(Long checkpointId, String inputPreview, String outputPreview, Integer inputSize, Integer outputSize) {
        this.checkpointId = checkpointId;
        this.inputPreview = inputPreview;
        this.outputPreview = outputPreview;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
    }
}