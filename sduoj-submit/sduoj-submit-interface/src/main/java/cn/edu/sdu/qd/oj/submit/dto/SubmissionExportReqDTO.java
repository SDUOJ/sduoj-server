/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionExportReqDTO extends BaseDTO {

    @NotNull(message = "contestId should not be null")
    @Range(min = 1)
    private Long contestId;

    private Long userId;

    private Long problemId;

    private Long judgeTemplateId;

    private Integer judgeResult;
}