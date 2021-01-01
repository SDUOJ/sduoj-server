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
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateListDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemCaseDTO;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestProblemDTO extends BaseDTO {

    // 脱敏后的含义是 problemIndex，脱敏前的含义是 problemCode
    private String problemCode;

    private Integer problemWeight;

    private String problemTitle;

    private List<String> languages;

    private Integer memoryLimit;

    private Integer timeLimit;

    private List<JudgeTemplateListDTO> judgeTemplates;

    // 其他字段，如在该比赛内的过题人数


    // ------------------------------------------

    private ContestProblemDescriptionDTO problemDescriptionDTO;

    private List<ProblemCaseDTO> problemCaseDTOList;
}