package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.pojo.Problem;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemJudgerBo;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemListBo;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemManageBo;
import cn.edu.sdu.qd.oj.problem.service.ProblemService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @PostMapping("/query")
    @ApiResponseBody
    public Problem queryById(@RequestBody Map json) {
        return this.problemService.queryById((Integer) json.get("problemId"));
    }

    @PostMapping("/list")
    @ApiResponseBody
    public PageResult<ProblemListBo> queryList(@RequestBody Map json) {
        int pageNow = (int) json.get("pageNow");
        int pageSize = (int) json.get("pageSize");
        PageResult<ProblemListBo> result = this.problemService.queryProblemByPage(pageNow, pageSize);
        if (result == null || result.getRows().size() == 0) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        return result;
    }
}
