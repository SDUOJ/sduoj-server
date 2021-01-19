/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemManageDTO extends BaseDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long problemId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtCreate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtModified;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<String, String> features;

    private String problemCode;

    private Integer isPublic;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;

    private String problemTitle;

    private String source;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String remoteOj;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String remoteUrl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer submitNum;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer acceptNum;

    private Integer memoryLimit;

    private Integer timeLimit;

    private Long defaultDescriptionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer checkpointNum;

    @Size(max = 1024)
    @Valid
    private List<ProblemCheckpointDTO> checkpoints;

    @Size(max = 20)
    private List<Long> checkpointCases;

    private List<Long> judgeTemplates;

    // ------------------------------------------

    private ProblemDescriptionDTO problemDescriptionDTO;

    private String username;
}