/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestSubmissionListReqDTO extends BaseDTO {
    private int pageNow;
    private int pageSize;
    private String sortBy;
    private Boolean ascending = false;

    @NotNull
    private Long contestId;

    private String username;
    private String problemCode;
    private Integer problemIndex;
    private Long userId;

    private String language;
    private Long judgeTemplateId;
    private Integer judgeResult;
}