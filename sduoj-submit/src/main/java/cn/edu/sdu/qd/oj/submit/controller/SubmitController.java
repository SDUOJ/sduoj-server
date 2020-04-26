/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.submit.pojo.Submission;
import cn.edu.sdu.qd.oj.submit.pojo.SubmissionListBo;
import cn.edu.sdu.qd.oj.submit.service.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @ClassName SubmitController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/6 16:03
 * @Version V1.0
 **/

@Controller
@RequestMapping("/submit")
public class SubmitController {

    @Autowired
    private SubmitService submitService;


    @PostMapping("/create")
    @ApiResponseBody
    public Long createSubmission(@RequestBody Map json,
                                 @RequestHeader("X-FORWARDED-FOR") String ipv4,
                                 @RequestHeader("authorization-userId") Integer userId) {
        int problemId = (int) json.get("problemId");
        int languageId = (int) json.get("languageId");
        String code = (String) json.get("code");
        Submission submission = new Submission(problemId, userId, languageId, ipv4, code);
        if (this.submitService.createSubmission(submission)) {
            return submission.getSubmissionId();
        }
        throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
    }

    @PostMapping("/query")
    @ApiResponseBody
    public Submission query(@RequestBody Map json,
                            @RequestHeader("authorization-userId") Integer userId,
                            @RequestHeader Map map) {
        int submissionId = (int) json.get("submissionId");
        Submission submission = this.submitService.queryById(submissionId);
        if (submission != null && !submission.getUserId().equals(userId)) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_MATCHING);
        }
        return submission;
    }

    @PostMapping("/list")
    @ApiResponseBody
    public PageResult<SubmissionListBo> queryList(@RequestBody Map json) {
        String username = (String) json.get("username");
        Integer problemId = (Integer) json.get("problemId");
        int pageNow = (int) json.get("pageNow");
        int pageSize = (int) json.get("pageSize");
        PageResult<SubmissionListBo> result = this.submitService.querySubmissionByPage(username, problemId, pageNow, pageSize);
        if (result == null || result.getRows().size() == 0) {
            throw new ApiException(ApiExceptionEnum.SUBMISSION_NOT_FOUND);
        }
        return result;
    }
}