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

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.problem.dto.ProblemJudgerDTO;
import cn.edu.sdu.qd.oj.problem.service.ProblemJudgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ProblemJudgerController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:32
 * @Version V1.0
 **/

@Controller
@RequestMapping("/judger/problem")
public class ProblemJudgerController {
    @Autowired
    private ProblemJudgerService problemJudgerService;

    @GetMapping("/query")
    @ApiResponseBody
    public ProblemJudgerDTO queryByCode(@RequestParam("problemId") Long problemId) {
        return this.problemJudgerService.queryById(problemId);
    }
}