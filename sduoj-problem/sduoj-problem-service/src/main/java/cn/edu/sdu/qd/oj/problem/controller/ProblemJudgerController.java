/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemJudgerDTO;
import cn.edu.sdu.qd.oj.problem.service.ProblemJudgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName ProblemJudgerController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:32
 * @Version V1.0
 **/

@Controller
@RequestMapping("/judger/problem")
public class ProblemJudgerController {
    @Autowired
    private ProblemJudgerService problemJudgerService;

    @GetMapping("/query")
    @ApiResponseBody
    public ProblemJudgerDTO queryByCode(@RequestParam("problemCode") String problemCode) {
        return this.problemJudgerService.queryByCode(problemCode);
    }
}