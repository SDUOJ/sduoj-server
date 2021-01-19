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

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionListReqDTO extends BaseDTO {
    private int pageNow;
    private int pageSize;
    private String sortBy;
    private Boolean ascending = false;

    private String username;
    private String problemCode;
    private Long problemId;
    private Long userId;

    private Long judgeTemplateId;
    private Integer judgeResult;

    private List<String> problemCodeList;
}