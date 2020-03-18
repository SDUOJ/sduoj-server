/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionJudgeBoMapper;
import cn.edu.sdu.qd.oj.submit.mapper.SubmissionMapper;
import cn.edu.sdu.qd.oj.submit.pojo.Submission;
import cn.edu.sdu.qd.oj.submit.pojo.SubmissionJudgeBo;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SubmitService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/6 16:04
 * @Version V1.0
 **/

@Service
@Slf4j
public class SubmitService {

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private SubmissionJudgeBoMapper submissionJudgeBoMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean createSubmission(Submission submission) {
        if (this.submissionMapper.insertSelective(submission) == 1) {
            try {
                Map<String, Object> msg = new HashMap<>();
                msg.put("event", "submissionCreated");
                msg.put("submissionId", submission.getId());
                this.rabbitTemplate.convertAndSend("", "judge_queue", msg);
            } catch (Exception e) {
                log.error("提交创建失败, ");
            }
            return true;
        }
        return false;
    }

    public SubmissionJudgeBo queryByJudger(int id) {
        SubmissionJudgeBo submissionJudgeBo = this.submissionJudgeBoMapper.selectByPrimaryKey(id);
        if (submissionJudgeBo == null) {
            throw new ApiException(ApiExceptionEnum.SUBMISSION_NOT_FOUND);
        }
        return submissionJudgeBo;
    }

    public void updateSubmission(Submission submission) {
        if(this.submissionMapper.updateByPrimaryKeySelective(submission) != 1)
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
    }
}