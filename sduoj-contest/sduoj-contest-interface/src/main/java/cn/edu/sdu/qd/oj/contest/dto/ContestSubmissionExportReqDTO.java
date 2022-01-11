/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestSubmissionExportReqDTO extends BaseDTO {

    @NotNull(message = "contestId should not be null")
    private Long contestId;

    private String username;

    private Integer problemIndex;

    private Long judgeTemplateId;

    private Integer judgeResult;

    /**
     * 是否过滤出第一个符合条件的
     */
    private Integer isFilteringFirstSubmission = 0;
}