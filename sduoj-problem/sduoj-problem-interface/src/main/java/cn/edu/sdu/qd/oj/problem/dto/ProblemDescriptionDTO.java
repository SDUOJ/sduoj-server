/*
 * Copyright 2020-2020 the original author or authors.
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemDescriptionDTO extends BaseDTO {

    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<String, String> features;

    private Integer isPublic;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long problemId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer voteNum;

    private String title;

    private String markdownDescription;

    private String htmlDescription;

    private String htmlInput;

    private String htmlOutput;

    private String htmlSampleInput;

    private String htmlSampleOutout;

    private String htmlHint;

    // -------------------------------

    @NotNull
    @NotBlank
    private String problemCode;
}