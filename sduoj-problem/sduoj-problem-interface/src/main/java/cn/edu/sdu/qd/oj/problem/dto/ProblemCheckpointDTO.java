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

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemCheckpointDTO extends BaseDTO {

    public static final int BYTE_SIZE = 12;

    @NotNull(message = "检查点id不可为空")
    private Long checkpointId;

    @NotNull(message = "检查点分数不可为空")
    private Integer checkpointScore;

}