/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.filter;

import cn.edu.sdu.qd.oj.config.FilterProperties;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @ClassName LoginFilter
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/21 21:16
 * @Version V1.0
 **/

@Slf4j
@Component
@EnableConfigurationProperties({FilterProperties.class})
public class LoginFilter implements GlobalFilter, Ordered {

    @Autowired
    private FilterProperties filterProp;

    /**
     * @Description TODO
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     **/
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().toString();
        if (!isAllowPath(requestUrl)) {
            // 取 token 并解密
            Object userinfo = Optional.ofNullable(exchange.getSession().block()).map(WebSession::getAttributes).map(map -> map.get("SDUOJUserInfo")).orElse(null);
            if (userinfo != null) {
                UserSessionDTO userSessionDTO = JSON.parseObject((String) userinfo, UserSessionDTO.class);
                if (userSessionDTO != null) {
                    // 装饰器 修改 getHeaders 方法
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            MultiValueMap<String, String> multiValueMap = CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap(8, Locale.ENGLISH));
                            super.getHeaders().forEach((key, value) -> multiValueMap.put(key, value));
//                            multiValueMap.remove("cookie"); // 在此处已解码 token, 故不下传省流量, 如果后续有多值 cookie 此处需要修改
                            multiValueMap.remove("SDUOJUserInfo");
                            multiValueMap.add("SDUOJUserInfo", JSON.toJSONString(userSessionDTO));
                            for (Field field : UserSessionDTO.class.getDeclaredFields()) {
                                try {
                                    field.setAccessible(true);
                                    multiValueMap.remove("Authorization-" + field.getName());
                                    multiValueMap.add("Authorization-" + field.getName(), String.valueOf(field.get(userSessionDTO)));
                                } catch (IllegalAccessException e) {
                                    log.error("getHeaders Decorator", e);
                                }
                            }
                            return new HttpHeaders(multiValueMap);
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                }
            }
        } else {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                List<String> userInfos = exchange.getResponse().getHeaders().remove("SDUOJUserInfo");
                Optional.of(userInfos).filter(list -> !list.isEmpty()).map(list -> list.get(0)).ifPresent(userInfoStr -> {
                    Optional.ofNullable(exchange.getSession().block()).map(WebSession::getAttributes).ifPresent(map -> {
                        if ("Logout".equals(userInfoStr)) {
                            map.remove("SDUOJUserInfo");
                        } else {
                            map.put("SDUOJUserInfo", userInfoStr);
                        }
                    });
                });
            }));
        }
        // 返回鉴权失败的消息
        ServerHttpResponse response = exchange.getResponse();
        Map<String, Object> message = new HashMap<>();
        message.put("code", HttpStatus.UNAUTHORIZED.value());
        message.put("message", "鉴权失败，无 token !");
        message.put("timestamp", (int) (System.currentTimeMillis() / 1000));
        message.put("data", null);
        byte[] bits = JSON.toJSONBytes(message);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    private boolean isAllowPath(String requestUrl) {
        for (String allowPath : filterProp.getAllowPaths())
            if (requestUrl.startsWith(allowPath))
                return true;
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}