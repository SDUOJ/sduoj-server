/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.common.util.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.common.util.LongToHexStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

    @JsonSerialize(using = LongToHexStringSerializer.class)
    @JsonDeserialize(using = HexStringToLongDeserializer.class)
    private Long checkpointId;

    private Date gmtCreate;

    private Date gmtModified;

    private String inputDescription;

    private String outputDescription;

    private Integer inputSize;

    private Integer outputSize;

    private String inputFileName;

    private String outputFileName;

    private String input;

    private String output;

    public CheckpointDTO(Long checkpointId, String inputDescription, String outputDescription, Integer inputSize, Integer outputSize) {
        this.checkpointId = checkpointId;
        this.inputDescription = inputDescription;
        this.outputDescription = outputDescription;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
    }
}