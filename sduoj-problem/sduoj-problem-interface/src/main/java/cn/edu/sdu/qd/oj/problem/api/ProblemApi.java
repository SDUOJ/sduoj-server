/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.api;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @InterfaceName ProblemApi
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/9 17:05
 * @Version V1.0
 **/

@RequestMapping("/internal/problem")
public interface ProblemApi {
    String SERVICE_NAME = "problem-service";

    @GetMapping("/queryIdToTitleMap")
    Map<Long, String> queryIdToTitleMap() throws InternalApiException;

    @PostMapping(value = "/validateProblemCodeList",consumes = "application/json")
    boolean validateProblemCodeList(@RequestBody List<String> problemCodeList) throws InternalApiException;

    /**
     * @Description 获取题目和指定描述模板，找不到时返回null; userId 为鉴权使用
     **/
    @GetMapping("/queryAndValidate")
    ProblemDTO queryAndValidate(@RequestParam("problemCode") String problemCode,
                                @RequestParam("problemDescriptionId") long problemDescriptionId,
                                @RequestParam("userId") long userId);
}