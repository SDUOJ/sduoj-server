/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @ClassName WebSocketServer
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/12 10:35
 * @Version V1.0
 **/

@ServerEndpoint("/submit/listen/{submissionId}")
@Component
@Slf4j
public class WebSocketServer {

    private static ConcurrentHashMap<Long, CopyOnWriteArraySet<WebSocketServer>> webSocketMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Long, StringBuffer> submitResultPool = new ConcurrentHashMap<>();

    private static ObjectMapper objectMapper = new ObjectMapper();

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
            // 缓存起消息来
            StringBuffer pool = submitResultPool.get(submissionId);
            if (pool == null) {
                pool = new StringBuffer("[");
                submitResultPool.put(submissionId, pool);
            }
            if (pool.length() != 1)
                pool.append(',');
            pool.append(message);
            // 批量发送
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
    public void onOpen(Session session, @PathParam("submissionId") Long submissionId) throws IOException {
        this.session = session;
        this.submissionId = submissionId;
        CopyOnWriteArraySet<WebSocketServer> webSocketServers = webSocketMap.get(submissionId);
        if (webSocketServers == null) {
            webSocketServers = new CopyOnWriteArraySet<>();
            webSocketServers.add(this);
            webSocketMap.put(submissionId, webSocketServers);
        } else {
            webSocketServers.add(this);
        }
        StringBuffer pool = submitResultPool.get(submissionId);
        if (pool != null)
            this.sendMessage(pool.toString() + "]");
        else
            this.sendMessage("[]");
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

    /**
    * @Description 供 /api/submit/update 调用
    * @param submissionId
    * @param json
    * @return void
    **/
    // TODO: 现在是较为糟糕的设计, 后期->减少耦合性、集群部署问题
    public static void finishJudge(Long submissionId, Map json) {
        try {
            sendInfo(objectMapper.writeValueAsString(json), submissionId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        submitResultPool.remove(submissionId);
    }
}