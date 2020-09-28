package cn.edu.sdu.qd.oj.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;


@Data
@EqualsAndHashCode
@AllArgsConstructor
public class WebSocketSender {

    private String uuid;
    private WebSocketSession session;
    private FluxSink<WebSocketMessage> sink;

    public void sendData(String data) {
        sink.next(session.textMessage(data));
    }
}
