/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.websocket.handler;

import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.websocket.constant.SubmissionBizContant;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName SubmissionListener
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/15 12:28
 * @Version V1.0
 **/

@Slf4j
@Component
public class SubmissionListenHandler {

    @Autowired
    private RedisUtils redisUtils;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "sduoj.submission.queue", durable = "true"),
            exchange = @Exchange(value = "sduoj.submission.exchange",
            ignoreDeclarationExceptions = "true"),
            key = {"submission.checkpoint.push"})
    )
    public void pushSubmissionResult(List list) {
        log.info("rabbitMQ: {}", list);
        String submissionId = (String) list.get(0);
        list.remove(0);
        redisUtils.lSet(SubmissionBizContant.getRedisSubmissionKey(submissionId), JSONObject.toJSONString(list),
                SubmissionBizContant.REDIS_SUBMISSION_RESULT_EXPIRE);
        redisUtils.publish(SubmissionBizContant.getRedisChannelKey(submissionId), JSONObject.toJSONString(list));
    }
}