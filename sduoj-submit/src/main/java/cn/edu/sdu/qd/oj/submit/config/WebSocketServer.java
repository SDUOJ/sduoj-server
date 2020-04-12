/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @ClassName WebSocketServer
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/12 10:35
 * @Version V1.0
 **/

@ServerEndpoint("/api/submit/listen/{submissionId}") // TODO: Gateway 组件更换以通过网关支持长连接
@Component
@Slf4j
public class WebSocketServer {

    private static ConcurrentHashMap<Long, CopyOnWriteArraySet<WebSocketServer>> webSocketMap = new ConcurrentHashMap<>();

    private Session session;

    private Long submissionId;

    /**
     * @description 发送报文到指定监听管道
     * @param message
     * @param submissionId
     * @return void
     **/
    public static void sendInfo(String message, @PathParam("userId") Long submissionId) throws IOException {
        log.info("[Submission WebSocket]: 发送消息到:" + submissionId + "，报文:" + message);
        if (submissionId != null) {
            CopyOnWriteArraySet<WebSocketServer> webSocketServers = webSocketMap.get(submissionId);
            if (webSocketServers != null) {
                webSocketMap.get(submissionId).parallelStream().forEach(ws -> {
                    try {
                        ws.sendMessage(message);
                    } catch (IOException ignore) {
                    }
                });
            }
        } else {
            log.error("用户" + submissionId + ",不在线！");
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("submissionId") Long submissionId) {
        this.session = session;
        this.submissionId = submissionId;
        CopyOnWriteArraySet<WebSocketServer> webSocketServers = webSocketMap.get(submissionId);
        if (webSocketServers == null) {
            webSocketServers = new CopyOnWriteArraySet<>();
            webSocketServers.add(this);
            webSocketMap.put(submissionId, webSocketServers);
        } else {
            webSocketServers.add(this );
        }
        log.info("[Submission WebSocket]: " + submissionId + " 连接!");
    }

    @OnClose
    public void onClose() {
        webSocketMap.get(submissionId).remove(this);
        log.info("[Submission WebSocket]: " + submissionId + " 退出!");
    }

    /**
     * 推送 String
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

}