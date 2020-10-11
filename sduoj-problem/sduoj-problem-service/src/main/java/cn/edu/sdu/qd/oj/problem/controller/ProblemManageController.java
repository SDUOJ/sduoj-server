/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.dto.*;
import cn.edu.sdu.qd.oj.problem.service.ProblemManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName ProblemManageController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:30
 * @Version V1.0
 **/

@Controller
@RequestMapping("/manage/problem")
@Slf4j
public class ProblemManageController {

    @Autowired
    private ProblemManageService problemManageService;

    @GetMapping("/query")
    @ApiResponseBody
    public ProblemManageDTO queryByCode(@RequestParam("problemCode") String problemCode,
                                        @UserSession UserSessionDTO userSessionDTO) {
        ProblemManageDTO problemManageDTO = this.problemManageService.queryByCode(problemCode);
        // 脱敏  TODO: 超级管理员能看
        if (problemManageDTO.getIsPublic() == 0 && userSessionDTO.userIdNotEquals(problemManageDTO.getUserId())) {
            problemManageDTO = null;
        }
        return problemManageDTO;
    }

    @PostMapping("/create")
    @ApiResponseBody
    public String createProblemManageBo(@RequestBody ProblemManageDTO problemManageDTO,
                                        @RequestHeader("authorization-userId") Long userId) {
        problemManageDTO.setUserId(userId);
        return this.problemManageService.createProblem(problemManageDTO);
    }

    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<ProblemManageListDTO> queryList(@Valid ProblemListReqDTO reqDTO,
                                                      @UserSession UserSessionDTO userSessionDTO) {
        return this.problemManageService.queryProblemByPage(reqDTO, userSessionDTO);
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void updateProblem(@RequestBody ProblemManageDTO problem) {
        log.info("updateProblem: {}", problem);
        if (problem.getProblemCode() == null) {
            throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
        }
        if (problem.getCheckpoints() != null) {
            problem.setCheckpointNum(problem.getCheckpoints().length / 8);
        }
        problemManageService.update(problem);
        return null;
    }

    @PostMapping("/createDescription")
    @ApiResponseBody
    public Long createDescription(@RequestBody ProblemDescriptionDTO problemDescriptionDTO,
                                  @RequestHeader("authorization-userId") Long userId) {
        problemDescriptionDTO.setUserId(userId);
        return problemManageService.createDescription(problemDescriptionDTO);
    }

    @PostMapping("/updateDescription")
    @ApiResponseBody
    public Void updateDescription(@RequestBody @NotNull ProblemDescriptionDTO problemDescriptionDTO,
                                  @RequestHeader("authorization-userId") Long userId) {
        if (problemDescriptionDTO.getId() == null) {
            throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
        }
        problemDescriptionDTO.setUserId(userId);
        problemManageService.updateDescription(problemDescriptionDTO);
        return null;
    }

    @GetMapping("/queryDescription")
    @ApiResponseBody
    public ProblemDescriptionDTO queryDescription(@RequestParam("descriptionId") long id,
                                                  @UserSession(nullable = true) UserSessionDTO userSessionDTO) {
        return problemManageService.queryDescription(id, userSessionDTO.getUserId());
    }

    @GetMapping("/queryDescriptionList")
    @ApiResponseBody
    public List<ProblemDescriptionListDTO> queryDescriptionList(@RequestParam("problemCode") String problemCode,
                                                                @UserSession(nullable = true) UserSessionDTO userSessionDTO) {
        return problemManageService.queryDescriptionList(problemCode, userSessionDTO.getUserId());
    }

}