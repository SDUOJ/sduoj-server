/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionListDTO;
import cn.edu.sdu.qd.oj.submit.service.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @ResponseBody
    public ResponseResult<String> createSubmission(@RequestBody Map json,
                                 @RequestHeader("X-FORWARDED-FOR") String ipv4,
                                 @RequestHeader("authorization-userId") Integer userId) {
        long problemId = (long) json.get("problemId");
        int languageId = (int) json.get("languageId");
        String code = (String) json.get("code");
        SubmissionDTO submissionDTO = new SubmissionDTO(problemId, userId, languageId, ipv4, code);
        if (this.submitService.createSubmission(submissionDTO)) {
            return ResponseResult.ok(Long.toHexString(submissionDTO.getSubmissionId()));
        }
        throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
    }

    @PostMapping("/query")
    @ApiResponseBody
    public SubmissionDTO query(@RequestBody Map json,
                               @RequestHeader("authorization-userId") Integer userId,
                               @RequestHeader Map map) {
        long submissionId = Long.valueOf((String) json.get("submissionId"), 16);
        SubmissionDTO submissionDTO = this.submitService.queryById(submissionId);
        if (submissionDTO != null && !submissionDTO.getUserId().equals(userId)) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_MATCHING);
        }
        return submissionDTO;
    }

    @PostMapping("/list")
    @ApiResponseBody
    public PageResult<SubmissionListDTO> queryList(@RequestBody Map json) {
        String username = (String) json.get("username");
        Long problemId = (Long) json.get("problemId");
        int pageNow = (int) json.get("pageNow");
        int pageSize = (int) json.get("pageSize");
        PageResult<SubmissionListDTO> result = this.submitService.querySubmissionByPage(username, problemId, pageNow, pageSize);
        if (result == null || result.getRows().size() == 0) {
            throw new ApiException(ApiExceptionEnum.SUBMISSION_NOT_FOUND);
        }
        return result;
    }
}