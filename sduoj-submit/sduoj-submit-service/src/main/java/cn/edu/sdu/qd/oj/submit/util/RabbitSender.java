package cn.edu.sdu.qd.oj.submit.util;

import cn.edu.sdu.qd.oj.submit.dto.SubmissionMessageDTO;
import cn.edu.sdu.qd.oj.submit.dto.SubmissionResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* @Description 封装一层工具层，用于发送 mq 消息
**/
@Slf4j
@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
    * @Description 发送评测/重测请求
    **/
    public boolean sendJudgeRequest(SubmissionMessageDTO messageDTO) {
        return send("sduoj.submission.submit", "", messageDTO);
    }

    /**
     * @Description 发送ac消息
     **/
    public boolean sendACMessage(SubmissionResultDTO messageDTO) {
        return send("sduoj.submission.ac", "", messageDTO);
    }

    private boolean send(String exchange, String routingKey, Object o) {
        for (int i = 0; i < 5; i++) {
            try {
                this.rabbitTemplate.convertAndSend(exchange, routingKey, o);
                return true;
            } catch (AmqpException e) {
                log.warn("sendOneJudgeResult", e);
                try {
                    Thread.sleep(i * 2000L);
                } catch (Throwable ignore) {
                }
            } catch (Exception e) {
                log.error("sendOneJudgeResult", e);
                return false;
            }
        }
        return false;
    }
}
