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

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
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
        // 超级管理员一定能看所有题
        if (PermissionEnum.SUPERADMIN.in(userSessionDTO)) {
            return problemManageDTO;
        }
        // 非公开且非自己出的题看不了
        if (problemManageDTO.getIsPublic() == 0 && userSessionDTO.userIdNotEquals(problemManageDTO.getUserId())) {
            problemManageDTO = null;
        }
        return problemManageDTO;
    }

    @PostMapping("/create")
    @ApiResponseBody
    public String createProblemManageBo(@RequestBody ProblemManageDTO problemManageDTO,
                                        @UserSession UserSessionDTO userSessionDTO) {
        problemManageDTO.setUserId(userSessionDTO.getUserId());
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
    public Void updateProblem(@RequestBody @Valid ProblemManageDTO problem,
                              @UserSession UserSessionDTO userSessionDTO) {
        log.info("updateProblem: {}", problem);
        AssertUtils.notNull(problem.getProblemCode(), ApiExceptionEnum.PARAMETER_ERROR);
        if (problem.getCheckpoints() != null) {
            problem.setCheckpointNum(problem.getCheckpoints().size());
        }
        problemManageService.update(problem, userSessionDTO);
        return null;
    }

    @PostMapping("/createDescription")
    @ApiResponseBody
    public Long createDescription(@RequestBody ProblemDescriptionDTO problemDescriptionDTO,
                                  @UserSession UserSessionDTO userSessionDTO) {
        problemDescriptionDTO.setUserId(userSessionDTO.getUserId());
        return problemManageService.createDescription(problemDescriptionDTO);
    }

    @PostMapping("/updateDescription")
    @ApiResponseBody
    public Void updateDescription(@RequestBody @NotNull ProblemDescriptionDTO problemDescriptionDTO,
                                  @UserSession UserSessionDTO userSessionDTO) {
        AssertUtils.notNull(problemDescriptionDTO.getId(), ApiExceptionEnum.PARAMETER_ERROR);
        // 超级管理员一定可以改
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO)) {
            problemDescriptionDTO.setUserId(userSessionDTO.getUserId());
        }
        problemManageService.updateDescription(problemDescriptionDTO);
        return null;
    }

    @GetMapping("/queryDescription")
    @ApiResponseBody
    public ProblemDescriptionDTO queryDescription(@RequestParam("descriptionId") long id,
                                                  @UserSession(nullable = true) UserSessionDTO userSessionDTO) {
        return problemManageService.queryDescription(id, userSessionDTO);
    }

    @GetMapping("/queryDescriptionList")
    @ApiResponseBody
    public List<ProblemDescriptionListDTO> queryDescriptionList(@RequestParam("problemCode") String problemCode,
                                                                @UserSession(nullable = true) UserSessionDTO userSessionDTO) {
        return problemManageService.queryDescriptionList(problemCode, userSessionDTO);
    }

}