/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.submit.dto.*;
import cn.edu.sdu.qd.oj.submit.service.SubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.validation.Valid;

/**
 * @ClassName SubmitController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/6 16:03
 * @Version V1.0
 **/

@Controller
@RequestMapping("/submit")
@Slf4j
public class SubmitController {

    @Autowired
    private SubmitService submitService;

    @PostMapping("/create")
    @ApiResponseBody
    public String createSubmission(@RequestBody @Valid SubmissionCreateReqDTO reqDTO,
                                   @RequestHeader("X-FORWARDED-FOR") String ipv4,
                                   @UserSession UserSessionDTO userSessionDTO) {
        reqDTO.setIpv4(ipv4);
        reqDTO.setUserId(userSessionDTO.getUserId());
        return Long.toHexString(this.submitService.createSubmission(reqDTO, 0));
    }

    @GetMapping("/query")
    @ApiResponseBody
    public SubmissionDTO query(@RequestParam("submissionId") String submissionIdHex,
                               @RequestHeader("authorization-userId") @Nullable Long userId) {
        long submissionId = Long.valueOf(submissionIdHex, 16);
        SubmissionDTO submissionDTO = this.submitService.queryById(submissionId, 0);
        // TODO: 超级管理员可以看所有代码
        if (submissionDTO != null && !submissionDTO.getUserId().equals(userId)) {
            submissionDTO.setCode(null);
        }
        return submissionDTO;
    }

    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<SubmissionListDTO> queryList(@Valid SubmissionListReqDTO reqDTO) throws Exception {
        log.info("submissionList: req:{}", reqDTO);
        reqDTO.setProblemCodeList(null); // 禁掉指定题目
        return this.submitService.querySubmissionByPage(reqDTO, 0);
    }
}