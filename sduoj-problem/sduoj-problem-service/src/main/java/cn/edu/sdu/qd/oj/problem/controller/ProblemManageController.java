/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemManageListDTO;
import cn.edu.sdu.qd.oj.problem.service.ProblemManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

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


    @PostMapping("/query")
    @ApiResponseBody
    public ProblemManageDTO queryManageBoById(@RequestBody Map json) {
        return this.problemManageService.queryById((Integer) json.get("problemId"));
    }

    @PostMapping("/create")
    @ApiResponseBody
    public Integer createProblemManageBo(@RequestBody ProblemManageDTO problem,
                                         @RequestHeader("authorization-userId") Integer userId) {
        problem.setUserId(userId);
        this.problemManageService.createProblem(problem);
        return problem.getProblemId();
    }


    @PostMapping("/list")
    @ApiResponseBody
    public PageResult<ProblemManageListDTO> queryList(@RequestBody Map json
    ) {
        int pageNow = (int) json.get("pageNow");
        int pageSize = (int) json.get("pageSize");
        PageResult<ProblemManageListDTO> result = this.problemManageService.queryProblemByPage(pageNow, pageSize);
        return result;
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void updateProblem(@RequestBody ProblemManageDTO problem) {
        log.warn("updateProblem: {}", problem);
        if (problem.getProblemId() == null)
            throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
        if (problem.getCheckpointIds() != null)
            problem.setCheckpointNum(problem.getCheckpointIds().length / 8);
        problemManageService.update(problem);
        return null;
    }


}