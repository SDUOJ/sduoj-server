/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.api;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.submit.dto.*;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionCreateReqDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListReqDTO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/internal/submit")
public interface SubmissionApi {
    String SERVICE_NAME = "submit-service";

    /**
    * @Description 创建一个提交，返回 submissionId
    **/
    @PostMapping(value = "/create", consumes = "application/json")
    long create(@RequestParam("contestId") long contestId,
                @RequestBody SubmissionCreateReqDTO reqDTO);

    @PostMapping(value = "/page", consumes = "application/json")
    PageResult<SubmissionListDTO> page(@RequestParam("contestId") long contestId,
                                       @RequestBody SubmissionListReqDTO reqDTO) throws InternalApiException;

    @PostMapping("/listResult")
    List<SubmissionResultDTO> listResult(@RequestParam("contestId") long contestId,
                                         @RequestParam(value = "userId", required = false) Long userId) throws InternalApiException;

    @GetMapping("query")
    SubmissionDTO query(@RequestParam("submissionId") long submissionId,
                        @RequestParam("contestId") long contestId) throws InternalApiException;

    /**
     * @Description 查询用户过题
     **/
    @GetMapping("/queryACProblem")
    List<String> queryACProblem(@RequestParam("userId") long userId,
                                @RequestParam("contestId") long contestId);

    /**
     * @Description 查询比赛提交过题情况
     **/
    @GetMapping("/queryContestSubmitAndAccept")
    List<ProblemListDTO> queryContestSubmitAndAccept(@RequestParam("contestId") long contestId);

    @PostMapping("/update")
    void update(@RequestBody @Valid SubmissionUpdateReqDTO reqDTO) throws InternalApiException;

    /**
    * @Description 查询指定版本的 submissionId，不存在则返回 null
    **/
    @GetMapping("/querySubmissionJudgeDTO")
    SubmissionJudgeDTO querySubmissionJudgeDTO(@RequestParam("submissionId") long submissionId,
                                               @RequestParam("version") int version);

    /**
    * @Description 使得提交无效
    **/
    @GetMapping("invalidateSubmission")
    boolean invalidateSubmission(@RequestParam("submissionId") long submissionId,
                                 @RequestParam("contestId") long contestId);
}