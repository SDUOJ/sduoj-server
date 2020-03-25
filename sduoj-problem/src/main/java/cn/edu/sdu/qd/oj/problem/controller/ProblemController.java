package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.pojo.Problem;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemJudgerBo;
import cn.edu.sdu.qd.oj.problem.pojo.ProblemListBo;
import cn.edu.sdu.qd.oj.problem.service.ProblemService;


import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @PostMapping("query")
    @ApiResponseBody
    public Problem queryById(@RequestBody Map json) {
        return this.problemService.queryById((Integer) json.get("id"));
    }

    @PostMapping("list")
    @ApiResponseBody
    public PageResult<ProblemListBo> queryList(@RequestBody Map json) {
        int page = (int) json.get("page");
        int limit = (int) json.get("limit");
        PageResult<ProblemListBo> result = this.problemService.queryProblemByPage(page, limit);
        if (result == null || result.getRows().size() == 0) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        return result;
    }

    @PostMapping("querybyjudger")
    @ApiResponseBody
    public ProblemJudgerBo queryByJudger(@RequestBody Map json) {
        return this.problemService.querybujudger((Integer) json.get("id"));
    }

}
