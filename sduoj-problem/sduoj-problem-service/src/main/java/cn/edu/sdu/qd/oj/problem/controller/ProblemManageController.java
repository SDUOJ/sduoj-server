/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.dto.*;
import cn.edu.sdu.qd.oj.problem.service.ProblemManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ProblemManageDTO queryByCode(@RequestParam("problemCode") String problemCode) {
        return this.problemManageService.queryByCode(problemCode);
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
    public PageResult<ProblemManageListDTO> queryList(@RequestParam("pageNow") int pageNow,
                                                      @RequestParam("pageSize") int pageSize) {
        PageResult<ProblemManageListDTO> result = this.problemManageService.queryProblemByPage(pageNow, pageSize);
        if (result == null || result.getRows().size() == 0) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        return result;
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

}