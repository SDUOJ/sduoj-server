/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.submit.pojo.SubmissionJudgeBo;
import cn.edu.sdu.qd.oj.submit.service.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName SubmitController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/6 16:03
 * @Version V1.0
 **/

@Controller
@CrossOrigin
public class SubmitController {

    @Autowired
    private SubmitService submitService;

    @GetMapping("/create/{id}")
    @ApiResponseBody
    public Void submit_test(@PathVariable("id") int id) {
        this.submitService.createSubmission(id);
        return null;
    }

    @PostMapping("/queryByJudger")
    @ApiResponseBody
    public SubmissionJudgeBo queryByJudger(@RequestBody Map json) {
        int id = (int) json.get("id");
        return this.submitService.queryByJudger(id);
    }
}