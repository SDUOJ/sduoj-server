/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.annotation.RealIp;
import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.submit.dto.*;
import cn.edu.sdu.qd.oj.submit.service.SubmitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Entrance of submission biz
 *
 * @author zhangt2333
 */

@Controller
@RequestMapping("/submit")
@Slf4j
public class SubmitController {

    @Autowired
    private SubmitService submitService;

    @PostMapping("/create")
    @ApiResponseBody
    public String createSubmission(@RequestBody @Valid SubmissionCreateReqDTO reqDTO,
                                   @RealIp String ipv4,
                                   @UserSession UserSessionDTO userSessionDTO) {
        // 特判 代码或文件 仅一个不空
        AssertUtils.isTrue(1 == (StringUtils.isNotBlank(reqDTO.getCode()) ? 1 : 0) + (Objects.nonNull(reqDTO.getZipFileId()) ? 1 : 0),
                ApiExceptionEnum.SUBMISSION_PARAM_ERROR);
        // 置入参
        reqDTO.setIpv4(ipv4);
        reqDTO.setUserId(userSessionDTO.getUserId());

        return Long.toHexString(this.submitService.createSubmission(reqDTO, 0));
    }

    @GetMapping("/query")
    @ApiResponseBody
    public SubmissionDTO query(@RequestParam("submissionId") String submissionIdHex,
                               @UserSession(nullable = true) UserSessionDTO userSessionDTO) {
        long submissionId = Long.valueOf(submissionIdHex, 16);
        SubmissionDTO submissionDTO = this.submitService.queryById(submissionId, 0);
        // 超级管理员可以看所有代码
        if (PermissionEnum.SUPERADMIN.in(userSessionDTO)) {
            return submissionDTO;
        }
        // 他人查看脱敏
        if (submissionDTO != null && !Optional.ofNullable(userSessionDTO)
                                              .map(o -> o.userIdEquals(submissionDTO.getUserId()))
                                              .orElse(false)) {
            submissionDTO.setCode(null);
            submissionDTO.setCheckpointResults(null);
            submissionDTO.setZipFileId(null);
            submissionDTO.setJudgeLog(null);
        }
        return submissionDTO;
    }

    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<SubmissionListDTO> page(@Valid SubmissionListReqDTO reqDTO,
                                              @UserSession(nullable = true) UserSessionDTO userSessionDTO) throws Exception {
        log.info("submissionList: req:{}", reqDTO);
        reqDTO.setProblemCodeList(null); // 暂不允许在该接口指定多题查询
        return this.submitService.querySubmissionByPage(reqDTO, 0, userSessionDTO);
    }

    @GetMapping("/queryACProblem")
    @ApiResponseBody
    public List<String> queryACProblem(@UserSession UserSessionDTO userSessionDTO) {
        return this.submitService.queryACProblem(userSessionDTO.getUserId(), 0);
    }

    @PostMapping("/rejudge")
    @ApiResponseBody
    public Void rejudge(@RequestBody @NotNull String[] submissionIdHexs,
                        @UserSession UserSessionDTO userSessionDTO) {
        AssertUtils.isTrue(PermissionEnum.ADMIN.in(userSessionDTO), ApiExceptionEnum.USER_NOT_MATCHING);
        List<Long> submissionIdList = Arrays.stream(submissionIdHexs).map(hex -> Long.valueOf(hex, 16)).collect(Collectors.toList());
        submitService.rejudge(submissionIdList);
        return null;
    }

    @GetMapping("/invalidateSubmission")
    @ApiResponseBody
    public Void invalidateSubmission(@RequestParam("submissionId") String submissionIdHex,
                                     @UserSession UserSessionDTO userSessionDTO) {
        long submissionId = Long.valueOf(submissionIdHex, 16);
        AssertUtils.isTrue(PermissionEnum.ADMIN.in(userSessionDTO), ApiExceptionEnum.USER_NOT_MATCHING);
        submitService.invalidateSubmission(submissionId, 0);
        return null;
    }
}