package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.problem.pojo.Problem;
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
@CrossOrigin
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping("{id}")
    @ApiResponseBody
    public Problem queryById(@PathVariable("id") Integer id) {
        return this.problemService.queryById(id);
    }

    // TODO: query the list of problem
}
