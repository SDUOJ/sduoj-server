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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestProblemManageListDTO extends BaseDTO {

    // 脱敏后的含义是 problemIndex，脱敏前的含义是 problemCode
    @Pattern(regexp = "^[^;]+$", message = "题目编码中不允许包含 ';' 号")
    @NotBlank
    private String problemCode;

    @Pattern(regexp = "^[^;]+$", message = "标题中不允许包含 ';' 号")
    @Length(max = 96, message = "题目标题长度超限")
    @NotBlank
    private String problemTitle;

    @NotNull
    private Integer problemWeight;

    @NotNull
    private Long problemDescriptionId;
}