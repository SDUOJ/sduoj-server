/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestDTO extends BaseDTO {

    private Long contestId;

    private Date gmtCreate;

    private Date gmtModified;

    private ContestFeatureDTO features;

    private Integer isPublic;

    private String contestTitle;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long userId;

    private Date gmtStart;

    private Date gmtEnd;

    private String source;

    private Integer participantNum;

    private String markdownDescription;

    private List<ContestProblemListDTO> problems;

    private List<String> participants; // List<username>

    // -----------------

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;
}