/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionJudgeDTO;
import cn.edu.sdu.qd.oj.submit.service.SubmitJudgerService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public SubmissionJudgeDTO query(@RequestBody Map json) {
        long submissionId = Long.valueOf((String) json.get("submissionId"), 16);
        return this.submitJudgerService.query(submissionId);
    }

    @PostMapping("/update")
    @ApiResponseBody
    public Void update(@RequestBody Map json) {
        long submissionId = Long.valueOf((String) json.get("submissionId"), 16);
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
        SubmissionDTO submissionDTO = new SubmissionDTO(submissionId, judgerId, judgeResult, judgeScore, usedTime, usedMemory, judgeLog);
        submissionDTO.setCheckpointResults(byteBuf.array());
        this.submitJudgerService.updateSubmission(submissionDTO);
        return null;
    }

}