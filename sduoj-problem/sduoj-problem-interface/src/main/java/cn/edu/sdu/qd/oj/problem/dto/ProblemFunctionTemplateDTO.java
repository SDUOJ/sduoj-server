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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemFunctionTemplateDTO extends BaseDTO {

    /**
     * 对应评测模板的 id
     */
    private Long judgeTemplateId;

    /**
     * 是否将 functionTemplate 显示给前端
     */
    private Integer isShowFunctionTemplate;

    /**
     * 函数模板, 将于用户代码进行上下拼接
     */
    private String functionTemplate;

    /**
     * 代码初始模板
     */
    private String initialTemplate;
}