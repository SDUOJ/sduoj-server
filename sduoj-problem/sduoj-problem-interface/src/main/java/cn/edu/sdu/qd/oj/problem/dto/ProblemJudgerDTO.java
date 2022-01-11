/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemJudgerDTO extends BaseDTO {

    private Long problemId;

    private Integer isPublic;

    private Integer timeLimit;

    private Integer memoryLimit;

    private Integer outputLimit;

    private Integer checkpointNum;

    private List<ProblemCheckpointDTO> checkpoints;

    private ProblemCheckerConfigDTO checkerConfig;

    private List<ProblemFunctionTemplateDTO> functionTemplates;
}