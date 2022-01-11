/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemFunctionTemplateDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListReqDTO;
import cn.edu.sdu.qd.oj.problem.service.ProblemService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping("/query")
    @ApiResponseBody
    public ProblemDTO queryByCode(@RequestParam("problemCode") String problemCode,
                                  @RequestParam("descriptionId") @Nullable Long descriptionId,
                                  @UserSession(nullable = true) UserSessionDTO userSessionDTO) {
        ProblemDTO problemDTO = this.problemService.queryByCode(problemCode, descriptionId, userSessionDTO);
        Optional.ofNullable(problemDTO.getFunctionTemplates()).ifPresent(functionTemplates -> {
            for (ProblemFunctionTemplateDTO functionTemplate : functionTemplates) {
                if (!Objects.equals(1, functionTemplate.getIsShowFunctionTemplate())) {
                    functionTemplate.setFunctionTemplate(null);
                }
            }
        });
        return problemDTO;
    }

    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<ProblemListDTO> queryList(@Valid ProblemListReqDTO problemListReqDTO,
                                                @UserSession(nullable = true) UserSessionDTO userSessionDTO) {
        return this.problemService.queryProblemByPage(problemListReqDTO, userSessionDTO);
    }
}