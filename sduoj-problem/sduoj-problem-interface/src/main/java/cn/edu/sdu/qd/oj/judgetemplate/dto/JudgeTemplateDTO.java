/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JudgeTemplateDTO extends BaseDTO {

    @NotNull
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtCreate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtModified;

    private String features;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer version;

    private Integer isPublic;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;

    private Integer type;

    private String title;

    private String shellScript;

    private Long zipFileId;

    private List<String> acceptFileExtensions;

    private String remoteOj;

    private String remoteParameters;

    private String comment;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long problemId;

    // -------------------------

    private String username;

    private String problemCode;
}