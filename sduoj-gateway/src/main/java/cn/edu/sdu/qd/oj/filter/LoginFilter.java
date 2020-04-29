/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.filter;

import cn.edu.sdu.qd.oj.auth.entity.UserInfo;
import cn.edu.sdu.qd.oj.auth.utils.JwtUtils;
import cn.edu.sdu.qd.oj.config.FilterProperties;
import cn.edu.sdu.qd.oj.config.JwtProperties;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName LoginFilter
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/21 21:16
 * @Version V1.0
 **/

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProp;

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @Description 对 token 鉴权, 解密后的信息存到 header, 而不下传 cookie
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     **/
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().toString();
        if (!isAllowPath(requestUrl)) {
            // 取 token 并解密
            HttpCookie cookie = exchange.getRequest().getCookies().getFirst(this.jwtProperties.getCookieName());
            String token = null;
            if (cookie != null && !StringUtils.isBlank(token = cookie.getValue())) {
                UserInfo userInfo = null;
                try {
                    userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
                } catch (Exception ignore) {
                }
                if (userInfo != null) {
                    UserInfo finalUserInfo = userInfo;
                    // 装饰器 修改 getHeaders 方法
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            MultiValueMap<String, String> multiValueMap = CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap(8, Locale.ENGLISH));
                            super.getHeaders().entrySet().stream().forEach(entry -> multiValueMap.put(entry.getKey(), entry.getValue()));
                            multiValueMap.remove("cookie"); // 在此处已解码 token, 故不下传省流量, 如果后续有多值 cookie 此处需要修改
                            for (Field field : UserInfo.class.getDeclaredFields()) {
                                try {
                                    field.setAccessible(true);
                                    multiValueMap.remove("Authorization-" + field.getName());
                                    multiValueMap.add("Authorization-" + field.getName(), String.valueOf(field.get(finalUserInfo)));
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                            return new HttpHeaders(multiValueMap);
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                }
            }
        } else {
            return chain.filter(exchange);
        }
        // 返回鉴权失败的消息
        ServerHttpResponse response = exchange.getResponse();
        Map<String, Object> message = new HashMap<>();
        message.put("code", HttpStatus.UNAUTHORIZED.value());
        message.put("message", "鉴权失败，无 token !");
        message.put("timestamp", (int) (System.currentTimeMillis() / 1000));
        message.put("data", null);
        byte[] bits = null;
        try {
            bits = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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