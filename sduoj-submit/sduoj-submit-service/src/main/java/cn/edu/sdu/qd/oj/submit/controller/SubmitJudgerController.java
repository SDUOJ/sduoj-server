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

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionUpdateReqDTO;
import cn.edu.sdu.qd.oj.submit.service.SubmitJudgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @ClassName SubmitJudgerController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:48
 * @Version V1.0
 **/
// TODO: controller 待处理
@Controller
@RequestMapping("/judger/submit")
public class SubmitJudgerController {

    @Autowired
    private SubmitJudgerService submitJudgerService;


//    @GetMapping("/query")
//    @ApiResponseBody
//    public SubmissionJudgeDTO query(@RequestParam("submissionId") String submissionIdHex) {
//        long submissionId = Long.valueOf(submissionIdHex, 16);
//        return this.submitJudgerService.query(submissionId, version);
//    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void update(@RequestBody @Valid SubmissionUpdateReqDTO reqDTO,
                       @RequestHeader("authorization-userId") Long userId) {
        reqDTO.setJudgerId(userId);
        this.submitJudgerService.updateSubmission(reqDTO);
        return null;
    }

}