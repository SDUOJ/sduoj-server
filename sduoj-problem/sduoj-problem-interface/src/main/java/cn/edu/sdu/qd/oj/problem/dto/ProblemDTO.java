/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateListDTO;
import cn.edu.sdu.qd.oj.tag.dto.TagDTO;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemDTO extends BaseDTO {

    private Long problemId;

    private Map<String, String> features;

    private String problemCode;

    private String problemTitle;

    private String source;

    private String remoteOj;

    private String remoteUrl;

    private Integer submitNum;

    private Integer acceptNum;

    private Integer memoryLimit;

    private Integer outputLimit;

    private Integer timeLimit;

    private Long defaultDescriptionId;

    private List<ProblemFunctionTemplateDTO> functionTemplates;

    // ------------------------------------------

    private ProblemDescriptionDTO problemDescriptionDTO;

    private List<ProblemDescriptionListDTO> problemDescriptionListDTOList;

    private List<TagDTO> tagDTOList;

    private List<JudgeTemplateListDTO> judgeTemplates;

    private List<ProblemCaseDTO> problemCaseDTOList;
}