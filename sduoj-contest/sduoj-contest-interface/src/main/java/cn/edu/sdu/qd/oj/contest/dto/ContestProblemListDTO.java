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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestProblemListDTO extends BaseDTO {

    private String problemCode;

    private String problemTitle;

    private Integer problemWeight;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long problemDescriptionId;

    private String problemColor;

    // -------------------------------- 其他字段，如在该比赛内的过题人数

    private int acceptNum;

    private int submitNum;

    private Integer judgeResult; // null 表示没交过该题

    private Integer judgeScore;  // null 表示没交过该题
}