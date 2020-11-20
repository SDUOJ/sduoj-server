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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtCreate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date gmtModified;

    @NotNull
    @Valid
    private ContestFeatureDTO features;

    @NotNull
    private Integer isPublic;

    @Length(max = 60, message = "标题最大长度为 60")
    @NotBlank
    @NotNull
    private String contestTitle;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long userId;

    private Date gmtStart;

    private Date gmtEnd;

    @Length(max = 60, message = "密码最大长度为 60")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String source;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer participantNum;

    private String markdownDescription;

    @Size(max = 96, message = "最多出 96 道题")
    private List<ContestProblemListDTO> problems;

    @Size(max = 1024, message = "最多支持 1024 个人参加比赛")
    private List<String> participants; // List<username>

    @Size(max = 1024, message = "最多支持 1024 个人参加比赛")
    private List<String> unofficialParticipants; // List<username>

    // -----------------

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;
}