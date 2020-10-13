/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.submit.api.SubmissionApi;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionCreateReqDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListReqDTO;
import cn.edu.sdu.qd.oj.submit.service.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SubmitInternalController implements SubmissionApi {

    @Autowired
    private SubmitService submitService;

    @Override
    public long create(long contestId, SubmissionCreateReqDTO reqDTO) {
        return submitService.createSubmission(reqDTO, contestId);
    }

    @Override
    public PageResult<SubmissionListDTO> list(long contestId, SubmissionListReqDTO reqDTO) throws InternalApiException {
        return submitService.querySubmissionByPage(reqDTO, contestId);
    }

    @Override
    public SubmissionDTO query(long submissionId, long contestId) throws InternalApiException {
        return submitService.queryById(submissionId, contestId);
    }

    @Override
    public List<String> queryACProblem(long userId, long contestId) {
        return submitService.queryACProblem(userId, contestId);
    }

    @Override
    public List<ProblemListDTO> queryContestSubmitAndAccept(long contestId) {
        return submitService.queryContestSubmitAndAccept(contestId);
    }
}