/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionUpdateReqDTO extends BaseDTO {

    @NotNull
    private Long submissionId;

    private Long judgerId;

    @NotNull
    private Integer version;

    @NotNull
    private Integer judgeResult;

    @NotNull
    private Integer judgeScore;

    @NotNull
    private Integer usedTime;

    @NotNull
    private Integer usedMemory;

    private String judgeLog;

    private List<EachCheckpointResult> checkpointResults;
}