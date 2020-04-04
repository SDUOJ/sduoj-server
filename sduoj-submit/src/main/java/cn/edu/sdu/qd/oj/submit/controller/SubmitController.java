/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.auth.entity.UserInfo;
import cn.edu.sdu.qd.oj.auth.utils.JwtUtils;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.utils.CookieUtils;
import cn.edu.sdu.qd.oj.submit.config.JwtProperties;
import cn.edu.sdu.qd.oj.submit.pojo.Submission;
import cn.edu.sdu.qd.oj.submit.pojo.SubmissionJudgeBo;
import cn.edu.sdu.qd.oj.submit.service.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
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
@EnableConfigurationProperties(JwtProperties.class)
public class SubmitController {

    @Autowired
    private SubmitService submitService;

    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("/create")
    @ApiResponseBody
    public Long createSubmission(@RequestBody Map json,
                                HttpServletRequest request) {
        int problemId = (int) json.get("problemId");
        int languageId = (int) json.get("languageId");
        String code = (String) json.get("code");
        String ipv4 = request.getHeader("X-FORWARDED-FOR");
        if (ipv4 == null) {
            ipv4 = request.getRemoteAddr();
        }
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        UserInfo userInfo;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        Submission submission = new Submission(problemId, userInfo.getUserId(), languageId, ipv4, code);
        if (this.submitService.createSubmission(submission)) {
            return submission.getSubmissionId();
        }
        throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
    }

    @PostMapping("/query")
    @ApiResponseBody
    public Submission query(@RequestBody Map json,
                            HttpServletRequest request) {
        int submissionId = (int) json.get("submissionId");
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        UserInfo userInfo;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        Submission submission = this.submitService.queryById(submissionId);
        if (submission != null && !submission.getUserId().equals(userInfo.getUserId())) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_MATCHING);
        }
        return submission;
    }
}