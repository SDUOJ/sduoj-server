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

/**
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/4 21:46
 * @Version V1.0
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemListDTO extends BaseDTO {

    private Long problemId;

    private Map<String, String> features;

    private String problemCode;

    private String problemTitle;

    private String source;

    private String remoteOj;

    private String remoteUrl;

    private Integer submitNum;

    private Integer acceptNum;

    // ---------------------

    private List<TagDTO> tagDTOList;
}