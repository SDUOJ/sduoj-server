/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.api;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemJudgerDTO;
import org.springframework.cache.annotation.Cacheable;
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

    @GetMapping("/problemIdToProblemTitle")
    @Cacheable(key = "#problemId", value = RedisConstants.PROBLEM_ID_TO_PROBLEM_TITLE)
    String problemIdToProblemTitle(@RequestParam("problemId") long problemId);

    @GetMapping("/problemIdToProblemCheckpointNum")
    @Cacheable(key = "#problemId", value = RedisConstants.PROBLEM_ID_TO_PROBLEM_CHECKPOINT_NUM)
    int problemIdToProblemCheckpointNum(@RequestParam("problemId") long problemId);

    @GetMapping("/problemCodeToProblemId")
    @Cacheable(key = "#problemCode", value = RedisConstants.PROBLEM_CODE_TO_PROBLEM_ID)
    Long problemCodeToProblemId(@RequestParam("problemCode") String problemCode);

    @GetMapping("/problemIdToProblemCode")
    @Cacheable(key = "#problemId", value = RedisConstants.PROBLEM_ID_TO_PROBLEM_CODE)
    String problemIdToProblemCode(@RequestParam("problemId") long problemId);



    @GetMapping("/queryIdToTitleMap")
    Map<Long, String> queryIdToTitleMap() throws InternalApiException;

    @PostMapping(value = "/validateProblemCodeList",consumes = "application/json")
    boolean validateProblemCodeList(@RequestBody List<String> problemCodeList) throws InternalApiException;

    /**
     * @Description 获取题目和指定描述模板，找不到时返回null; userId 为鉴权使用
     **/
    @GetMapping("/queryAndValidate")
    ProblemDTO queryProblemWithDescriptionId(@RequestParam("problemCode") String problemCode,
                                             @RequestParam("problemDescriptionId") long problemDescriptionId);

    /**
    * @Description 获取非该用户出题且不 public 题目的 id 列表
    **/
    @GetMapping("/queryPrivateProblemIdList")
    List<Long> queryPrivateProblemIdList(@RequestParam(value = "userId", required = false) Long exclusiveUserId);

    @GetMapping("/queryProblemJudgeDTO")
    ProblemJudgerDTO queryProblemJudgeDTO(@RequestParam("problemId") Long problemId);
}