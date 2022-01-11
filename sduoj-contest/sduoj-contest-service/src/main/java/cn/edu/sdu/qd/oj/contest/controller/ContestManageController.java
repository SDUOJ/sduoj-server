/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.controller;

import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.contest.client.ProblemClient;
import cn.edu.sdu.qd.oj.contest.dto.*;
import cn.edu.sdu.qd.oj.contest.service.ContestCommonService;
import cn.edu.sdu.qd.oj.contest.service.ContestManageService;
import cn.edu.sdu.qd.oj.contest.service.ContestService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@Slf4j
@Controller
@RequestMapping("/manage/contest")
public class ContestManageController {

    @Autowired
    private ContestService contestService;

    @Autowired
    private ContestManageService contestManageService;

    @Autowired
    private ProblemClient problemClient;

    @Autowired
    private ContestCommonService contestCommonService;

    @PostMapping("/create")
    @ApiResponseBody
    public Long create(@RequestBody @Valid ContestCreateReqDTO reqDTO,
                       @UserSession UserSessionDTO userSessionDTO) {
        // 增补
        reqDTO.setUserId(userSessionDTO.getUserId());

        // 校验 当前时间<开始时间<结束时间
        AssertUtils.isTrue(reqDTO.getGmtStart().after(new Date()), ApiExceptionEnum.CONTEST_TIME_ERROR);
        AssertUtils.isTrue(reqDTO.getGmtStart().before(reqDTO.getGmtEnd()), ApiExceptionEnum.CONTEST_TIME_ERROR);

        // 校验 problemCode
        try {
            List<String> problemCodeList = reqDTO.getProblems()
                    .stream()
                    .map(ContestProblemManageListDTO::getProblemCode)
                    .collect(Collectors.toList());
            AssertUtils.isTrue(problemClient.validateProblemCodeList(problemCodeList), ApiExceptionEnum.PROBLEM_NOT_FOUND);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }

        // TODO: 校验 problemDescriptionId

        // 挂星参赛者必须是参赛者子集
        List<String> participants = Optional.ofNullable(reqDTO.getParticipants()).orElse(Lists.newArrayList());
        List<String> unofficialParticipants = Optional.ofNullable(reqDTO.getUnofficialParticipants()).orElse(Lists.newArrayList());
        participants.addAll(unofficialParticipants);
        reqDTO.setParticipants(participants.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
        reqDTO.setUnofficialParticipants(unofficialParticipants.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));

        return contestManageService.create(reqDTO);
    }

    @GetMapping("/page")
    @ApiResponseBody
    public PageResult<ContestListDTO> page(ContestListReqDTO reqDTO,
                                           @UserSession UserSessionDTO userSessionDTO) {
        return contestService.page(reqDTO, userSessionDTO);
    }

    @GetMapping("/query")
    @ApiResponseBody
    public ContestManageDTO query(@RequestParam("contestId") long contestId,
                                  @UserSession UserSessionDTO userSessionDTO) {
        ContestManageDTO contestManageDTO = contestManageService.query(contestId);
        // 超级管理员、创建者、权限组成员 可查到比赛详情
        AssertUtils.isTrue(contestCommonService.isContestManager(contestManageDTO, userSessionDTO),
                        ApiExceptionEnum.USER_NOT_MATCHING, "非比赛创建人或权限组成员");
        return contestManageDTO;
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void update(@RequestBody @Valid ContestManageDTO reqDTO,
                       @UserSession UserSessionDTO userSessionDTO) {
        log.info("{}", reqDTO);
        // 校验 开始时间<结束时间
        AssertUtils.isTrue(reqDTO.getGmtStart().before(reqDTO.getGmtEnd()), ApiExceptionEnum.CONTEST_TIME_ERROR);

        // 挂星参赛者必须是参赛者子集
        List<String> participants = Optional.ofNullable(reqDTO.getParticipants()).orElse(Lists.newArrayList());
        List<String> unofficialParticipants = Optional.ofNullable(reqDTO.getUnofficialParticipants()).orElse(Lists.newArrayList());
        participants.addAll(unofficialParticipants);
        reqDTO.setParticipants(participants.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
        reqDTO.setUnofficialParticipants(unofficialParticipants.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));

        contestManageService.update(reqDTO, userSessionDTO);
        return null;
    }

    @GetMapping("/exportSubmission")
    public void exportSubmission(@Valid ContestSubmissionExportReqDTO reqDTO,
                                 @UserSession UserSessionDTO userSessionDTO,
                                 HttpServletResponse response) throws IOException {
        log.warn("exportSubmission: {}", reqDTO);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +
                URLEncoder.encode(String.valueOf(System.currentTimeMillis()), "UTF-8") + ".zip");
        response.setContentType("application/zip; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        contestManageService.exportSubmission(reqDTO, userSessionDTO, new ZipOutputStream(response.getOutputStream()));
    }
}