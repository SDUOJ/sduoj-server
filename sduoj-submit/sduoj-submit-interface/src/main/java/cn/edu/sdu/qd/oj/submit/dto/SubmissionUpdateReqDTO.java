/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.common.util.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.submit.util.CheckpointResultsToByteDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionUpdateReqDTO extends BaseDTO {

    @NotNull
    @JsonDeserialize(using = HexStringToLongDeserializer.class)
    private Long submissionId;

    private Long judgerId;

    @NotNull
    private Integer judgeResult;

    @NotNull
    private Integer judgeScore;

    @NotNull
    private Integer usedTime;

    @NotNull
    private Integer usedMemory;

    private String judgeLog;

    @NotNull
    @JsonDeserialize(using = CheckpointResultsToByteDeserializer.class)
    private byte[] checkpointResults;
}