/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateListDTO;
import cn.edu.sdu.qd.oj.tag.dto.TagDTO;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemManageListDTO extends BaseDTO {

    private Long problemId;

    private Date gmtCreate;

    private Date gmtModified;

    private Map<String, String> features;

    private String problemCode;

    private Integer isPublic;

    private Long userId;

    private String problemTitle;

    private String source;

    private String remoteOj;

    private String remoteUrl;

    private Integer submitNum;

    private Integer acceptNum;

    private Integer memoryLimit;

    private Integer timeLimit;

    private Long defaultDescriptionId;

    private Integer checkpointNum;

    private List<Long> judgeTemplates;

    // ------------------------------------------

    private List<TagDTO> tagDTOList;

    private String username;

    private List<JudgeTemplateListDTO> judgeTemplateListDTOList;
}