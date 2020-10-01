package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemListReqDTO;
import cn.edu.sdu.qd.oj.problem.service.ProblemService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping("/query")
    @ApiResponseBody
    public ProblemDTO queryByCode(@RequestParam("problemCode") String problemCode) {
        return this.problemService.queryByCode(problemCode);
    }

    @GetMapping("/list")
    @ApiResponseBody
    public PageResult<ProblemListDTO> queryList(@Valid ProblemListReqDTO problemListReqDTO) {
        PageResult<ProblemListDTO> result = this.problemService.queryProblemByPage(problemListReqDTO);
        if (result == null || result.getRows().size() == 0) {
            throw new ApiException(ApiExceptionEnum.PROBLEM_NOT_FOUND);
        }
        return result;
    }
}
