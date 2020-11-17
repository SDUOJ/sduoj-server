package cn.edu.sdu.qd.oj.problem.listener;

import cn.edu.sdu.qd.oj.problem.service.ProblemService;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionMessageDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Slf4j
@Component
public class ProblemListener {

    @Autowired
    private ProblemService problemService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "sduoj.submission.ac.problem", durable = "true"),
            exchange = @Exchange(value = "sduoj.submission.ac", ignoreDeclarationExceptions = "true")
    ))
    public void acSubmissionHandler(SubmissionResultDTO messageDTO) {
        log.info("incProblemAcceptNumber {} {}", messageDTO.getSubmissionId(), messageDTO.getProblemId());
        // 依赖定时任务来校准真正 ac 的数目，即去掉重测的 ac 数
        if (Objects.equals(0L, messageDTO.getContestId())) {
            problemService.incProblemAcceptNumber(messageDTO.getProblemId());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "sduoj.submission.submit.problem", durable = "true"),
            exchange = @Exchange(value = "sduoj.submission.submit", ignoreDeclarationExceptions = "true")
    ))
    public void submitSubmissionHandler(SubmissionMessageDTO messageDTO) {
        log.info("incProblemSubmitNumber {}", messageDTO.getSubmissionId());
        if (messageDTO.getProblemId() != null && Objects.equals(0L, messageDTO.getContestId())) {
            problemService.incProblemSubmitNumber(messageDTO.getProblemId());
        }
    }
}