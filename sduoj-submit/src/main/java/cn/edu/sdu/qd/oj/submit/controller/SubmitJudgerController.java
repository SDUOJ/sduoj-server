/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.submit.config.WebSocketServer;
import cn.edu.sdu.qd.oj.submit.pojo.Submission;
import cn.edu.sdu.qd.oj.submit.pojo.SubmissionJudgeBo;
import cn.edu.sdu.qd.oj.submit.service.SubmitJudgerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SubmitJudgerController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:48
 * @Version V1.0
 **/

@Controller
@RequestMapping("/judger/submit")
public class SubmitJudgerController {

    @Autowired
    private SubmitJudgerService submitJudgerService;


    @PostMapping("/query")
    @ApiResponseBody
    public SubmissionJudgeBo query(@RequestBody Map json) {
        long submissionId = (long) json.get("submissionId");
        return this.submitJudgerService.query(submissionId);
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void update(@RequestBody Map json) {
        long submissionId = (long) json.get("submissionId");
        int judgerId = (int) json.get("judgerId");
        int judgeResult = (int) json.get("judgeResult");
        int judgeScore = (int) json.get("judgeScore");
        int usedTime = (int) json.get("usedTime");
        int usedMemory = (int) json.get("usedMemory");
        String judgeLog = (String) json.get("judgeLog");
        List<List<Integer>> checkpointResult = (List<List<Integer>>) json.get("checkpointResults");
        ByteBuf byteBuf = Unpooled.buffer(checkpointResult.size() * 9);
        checkpointResult.forEach(result -> {
            byteBuf.writeByte(result.get(0).byteValue());
            byteBuf.writeInt(result.get(1));
            byteBuf.writeInt(result.get(2));
        });
        Submission submission = new Submission(submissionId, judgerId, judgeResult, judgeScore, usedTime, usedMemory, judgeLog);
        submission.setCheckpointResults(byteBuf.array());
        this.submitJudgerService.updateSubmission(submission);
        WebSocketServer.finishJudge(Long.valueOf(submissionId), json);
        return null;
    }

}