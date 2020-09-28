package cn.edu.sdu.qd.oj.websocket.config;

import cn.edu.sdu.qd.oj.websocket.constant.SubmissionBizContant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;



@Configuration
public class RedisListenerConfiguration {

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(listenerAdapter, new PatternTopic(SubmissionBizContant.REDIS_CHANNEL_PATTERN));

        return container;
    }
}