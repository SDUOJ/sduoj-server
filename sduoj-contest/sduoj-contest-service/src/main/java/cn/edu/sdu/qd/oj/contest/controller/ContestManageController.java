/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.ProblemCacheUtils;
import cn.edu.sdu.qd.oj.contest.client.ProblemClient;
import cn.edu.sdu.qd.oj.contest.dto.ContestCreateReqDTO;
import cn.edu.sdu.qd.oj.contest.dto.ContestProblemListDTO;
import cn.edu.sdu.qd.oj.contest.service.ContestManageService;
import cn.edu.sdu.qd.oj.problem.api.ProblemApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/manage/contest")
public class ContestManageController {

    @Autowired
    private ContestManageService contestManageService;

    @Autowired
    private ProblemClient problemClient;

    @PostMapping("/create")
    @ApiResponseBody
    public Long create(@RequestBody ContestCreateReqDTO reqDTO,
                       @RequestHeader("authorization-userId") Long userId) {
        // 增补
        reqDTO.setUserId(userId);

        // 校验 problemCode
        try {
            List<String> problemCodeList = reqDTO.getProblems()
                    .stream()
                    .map(ContestProblemListDTO::getProblemCode)
                    .collect(Collectors.toList());
            AssertUtils.isTrue(problemClient.validateProblemCodeList(problemCodeList), ApiExceptionEnum.PROBLEM_NOT_FOUND);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }

        // TODO: 校验 problemDescriptionId



        return contestManageService.create(reqDTO);
    }


}