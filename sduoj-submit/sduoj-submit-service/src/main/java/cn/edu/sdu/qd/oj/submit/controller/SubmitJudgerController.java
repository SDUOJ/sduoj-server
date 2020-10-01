/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionJudgeDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionUpdateReqDTO;
import cn.edu.sdu.qd.oj.submit.service.SubmitJudgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName SubmitJudgerController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:48
 * @Version V1.0
 **/

@Controller
@RequestMapping("/judger/submit")
public class SubmitJudgerController {

    @Autowired
    private SubmitJudgerService submitJudgerService;


    @GetMapping("/query")
    @ApiResponseBody
    public SubmissionJudgeDTO query(@RequestParam("submissionId") String submissionIdHex) {
        long submissionId = Long.valueOf(submissionIdHex, 16);
        return this.submitJudgerService.query(submissionId);
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void update(@RequestBody SubmissionUpdateReqDTO reqDTO,
                       @RequestHeader("authorization-userId") Long userId) {
        reqDTO.setJudgerId(userId);
        this.submitJudgerService.updateSubmission(reqDTO);
        return null;
    }

}