/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.problem.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @ClassName ProblemInternalController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/9 15:46
 * @Version V1.0
 **/

@Controller
@RequestMapping("/internal/problem")
public class ProblemInternalController {

    @Autowired
    private ProblemService problemService;

    @GetMapping("/queryid2title")
    @ResponseBody
    Map<Integer, String> queryAll() {
        return problemService.queryIdToTitleMap();
    }
}