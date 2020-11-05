package cn.edu.sdu.qd.oj.problem.listener;

import cn.edu.sdu.qd.oj.problem.service.ProblemService;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ProblemListener {

    @Autowired
    private ProblemService problemService;

    @RabbitListener(queues = "sduoj.submission.ac")
    public void acSubmissionHandler(SubmissionResultDTO messageDTO) {
        log.info("incProblemAcceptNumber {} {}", messageDTO.getSubmissionId(), messageDTO.getProblemId());
        problemService.incProblemAcceptNumber(messageDTO.getProblemId());
    }
}