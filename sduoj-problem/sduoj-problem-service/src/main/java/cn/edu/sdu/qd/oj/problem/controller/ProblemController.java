/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListReqDTO;
import cn.edu.sdu.qd.oj.problem.service.ProblemService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping("/query")
    @ApiResponseBody
    public ProblemDTO queryByCode(@RequestParam("problemCode") String problemCode,
                                  @RequestParam("descriptionId") @Nullable Long descriptionId,
                                  @RequestHeader("authorization-userId") @Nullable Long userId) {
        return this.problemService.queryByCode(problemCode, descriptionId, userId);
    }

    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<ProblemListDTO> queryList(@Valid ProblemListReqDTO problemListReqDTO,
                                                @RequestHeader("authorization-userId") @Nullable Long userId) {
        PageResult<ProblemListDTO> result = this.problemService.queryProblemByPage(problemListReqDTO, userId);
        if (result == null || result.getRows().size() == 0) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        return result;
    }
}