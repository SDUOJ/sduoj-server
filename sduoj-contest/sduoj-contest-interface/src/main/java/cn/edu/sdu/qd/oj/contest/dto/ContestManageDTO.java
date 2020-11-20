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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestManageDTO extends BaseDTO {

    private Long contestId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtCreate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtModified;

    @NotNull
    @Valid
    private ContestFeatureDTO features;

    @NotNull
    private Integer isPublic;

    private String contestTitle;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;

    private Date gmtStart;

    private Date gmtEnd;

    private String password;

    private String source;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer participantNum;

    private String markdownDescription;

    private List<ContestProblemManageListDTO> problems;

    private List<String> participants; // List<username>

    private List<String> unofficialParticipants;

    // -----------------

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;
}