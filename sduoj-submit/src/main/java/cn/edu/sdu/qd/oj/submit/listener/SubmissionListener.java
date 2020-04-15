/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.listener;

import cn.edu.sdu.qd.oj.submit.config.WebSocketServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName SubmissionListener
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/15 12:28
 * @Version V1.0
 **/

@Component
public class SubmissionListener {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "sduoj.submission.queue", durable = "true"),
            exchange = @Exchange(value = "sduoj.submission.exchange",
            ignoreDeclarationExceptions = "true"),
            key = {"submission.checkpoint.push"})
    )
    public void pushSubmissionResult(List list) throws Exception {
        Long submissionId = Long.valueOf((String) list.get(0));
        WebSocketServer.sendInfo(objectMapper.writeValueAsString(list), submissionId);
    }


}