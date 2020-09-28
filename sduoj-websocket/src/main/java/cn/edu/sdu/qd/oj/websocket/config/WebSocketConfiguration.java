package cn.edu.sdu.qd.oj.websocket.config;

import cn.edu.sdu.qd.oj.websocket.annotation.WebSocketMappingHandlerMapping;
import cn.edu.sdu.qd.oj.websocket.entity.WebSocketSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class WebSocketConfiguration {
 
	@Bean
	public HandlerMapping webSocketMapping() {
		return new WebSocketMappingHandlerMapping();
	}
 
	@Bean
	public ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSender>> senderMap() {
		return new ConcurrentHashMap<>();
	}
 
	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter();
	}
}