package cn.edu.sdu.qd.oj.websocket.handler;

import cn.edu.sdu.qd.oj.websocket.entity.WebSocketSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RedisListenHandler extends MessageListenerAdapter {

    @Autowired
    private ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSender>> senderMap;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        super.onMessage(message, pattern);
        String patternStr = new String(pattern);
        String channelStr = new String(message.getChannel());
        // TODO: better way to extract 'key'
        String submissionId = channelStr.substring(channelStr.lastIndexOf('/') + 1);
        String msg = new String(message.getBody());
        log.info("{} {} {}", patternStr, channelStr, msg);
        ConcurrentHashMap<String, WebSocketSender> webSocketSenderMap = senderMap.get(submissionId);
        if (webSocketSenderMap != null) {
            webSocketSenderMap.forEach((k, v) -> {
                v.sendData(msg);
            });
        }
    }
}
