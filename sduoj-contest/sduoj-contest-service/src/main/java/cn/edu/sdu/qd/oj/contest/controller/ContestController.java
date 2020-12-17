/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.controller;

import cn.edu.sdu.qd.oj.common.annotation.RealIp;
import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.CollectionUtils;
import cn.edu.sdu.qd.oj.contest.dto.*;
import cn.edu.sdu.qd.oj.contest.service.ContestService;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/contest")
public class ContestController {

    @Autowired
    private ContestService contestService;


    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<ContestListDTO> list(ContestListReqDTO reqDTO,
                                          @UserSession(nullable=true) UserSessionDTO userSessionDTO) {
        return contestService.page(reqDTO, userSessionDTO);
    }

    @PostMapping("/participate")
    @ApiResponseBody
    public Void participate(@RequestBody Map<String, String> json,
                            @UserSession UserSessionDTO userSessionDTO) {
        String password = json.get("password");
        Long contestId = Long.valueOf(json.get("contestId"));
        this.contestService.participate(contestId, userSessionDTO.getUserId(), password);
        return null;
    }

    @GetMapping("/query")
    @ApiResponseBody
    public ContestDTO queryDetail(@RequestParam("contestId") @NotBlank Long contestId,
                                  @UserSession UserSessionDTO userSessionDTO) throws InternalApiException {
        ContestDTO contestDTO = contestService.queryDetail(contestId, userSessionDTO);
        // 倘若比赛已开始但仍取的是比赛未开始时的缓存，则清除缓存再查一遍
        if (contestDTO != null && CollectionUtils.isEmpty(contestDTO.getProblems()) && contestDTO.getGmtStart().before(new Date())) {
            contestService.clearContestDetailCache(contestId, userSessionDTO);
            contestDTO = contestService.queryDetail(contestId, userSessionDTO);
        }
        return contestDTO;
    }

    @GetMapping("/queryUpcomingContest")
    @ApiResponseBody
    public ContestListDTO queryUpcomingContest() {
        return contestService.queryUpcomingContest();
    }


    @GetMapping("/queryProblem")
    @ApiResponseBody
    public ContestProblemDTO queryProblem(@RequestParam("contestId") @NotBlank Long contestId,
                                          @RequestParam("problemCode") @NotNull Integer problemIndex,
                                          @UserSession UserSessionDTO userSessionDTO) {
        ContestProblemDTO contestProblemDTO = contestService.queryProblem(contestId, problemIndex, userSessionDTO.getUserId());
        // 脱敏
        contestProblemDTO.setProblemCode(problemIndex.toString());
        return contestProblemDTO;
    }

    @PostMapping("/createSubmission")
    @ApiResponseBody
    public String submitCode(@RequestBody @Valid ContestSubmissionCreateReqDTO reqDTO,
                             @RealIp String ipv4,
                             @UserSession UserSessionDTO userSessionDTO) {
        // 增补数据
        reqDTO.setIpv4(ipv4);
        reqDTO.setUserId(userSessionDTO.getUserId());
        try {
            reqDTO.setProblemIndex(Integer.parseInt(reqDTO.getProblemCode()));
        } catch (Exception e){
            throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
        }
        return contestService.createSubmission(reqDTO);
    }

    @GetMapping("/querySubmission")
    @ApiResponseBody
    public SubmissionDTO querySubmssion(@RequestParam("submissionId") String submissionIdHex,
                                        @RequestParam("contestId") long contestId,
                                        @UserSession UserSessionDTO userSessionDTO) throws InternalApiException {
        Long submissionId = Long.valueOf(submissionIdHex, 16);
        return contestService.querySubmission(submissionId, contestId, userSessionDTO);
    }


    @GetMapping("/listSubmission")
    @ApiResponseBody
    public PageResult<ContestSubmissionListDTO> listSubmission(@Valid ContestSubmissionListReqDTO reqDTO,
                                                               @UserSession UserSessionDTO userSessionDTO) {
        Optional.of(reqDTO).map(ContestSubmissionListReqDTO::getProblemCode).filter(StringUtils::isNotEmpty).ifPresent(problemCode -> {
            try {
                reqDTO.setProblemIndex(Integer.parseInt(reqDTO.getProblemCode()));
            } catch (Exception e){
                throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR, "problemCode 非法");
            }
        });
        return contestService.listSubmission(reqDTO, userSessionDTO);
    }

//    @GetMapping("/queryACProblem")
//    @ApiResponseBody
//    public List<String> queryACProblem(@RequestParam("contestId") long contestId,
//                                       @UserSession UserSessionDTO userSessionDTO) {
//        return contestService.queryACProblem(userSessionDTO.getUserId(), contestId);
//    }

    @GetMapping("/rank")
    @ApiResponseBody
    public List<ContestRankDTO> queryRank(@RequestParam("contestId") long contestId,
                                          @UserSession UserSessionDTO userSessionDTO) throws InternalApiException {
        return contestService.queryRank(contestId, userSessionDTO);
    }

    @GetMapping("/invalidateSubmission")
    @ApiResponseBody
    public Void invalidateSubmission(@RequestParam("contestId") long contestId,
                                     @RequestParam("submissionId") String submissionIdHex,
                                     @UserSession UserSessionDTO userSessionDTO) {
        long submissionId = Long.valueOf(submissionIdHex, 16);
        contestService.invalidateSubmission(contestId, submissionId, userSessionDTO);
        return null;
    }
}