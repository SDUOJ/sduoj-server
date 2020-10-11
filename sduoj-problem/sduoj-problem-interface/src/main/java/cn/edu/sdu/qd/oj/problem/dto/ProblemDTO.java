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

    private List<String> languages;

    private Integer memoryLimit;

    private Integer timeLimit;

    private Long defaultDescriptionId;

    // ------------------------------------------

    private ProblemDescriptionDTO problemDescriptionDTO;

    private List<ProblemDescriptionListDTO> problemDescriptionListDTOList;

    private List<TagDTO> tagDTOList;
}