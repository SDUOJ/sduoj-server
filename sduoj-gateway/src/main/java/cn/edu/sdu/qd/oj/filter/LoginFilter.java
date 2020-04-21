/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.filter;

import cn.edu.sdu.qd.oj.config.FilterProperties;
import cn.edu.sdu.qd.oj.config.JwtProperties;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;

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
    private JwtProperties properties;

    @Autowired
    private FilterProperties filterProp;

    /**
    * @Description 拦截所有请求头
    * @param exchange
    * @param chain
    * @return reactor.core.publisher.Mono<java.lang.Void>
    **/
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().toString();
        if (!CollectionUtil.contains(this.filterProp.getAllowPaths(), requestUrl)) {
            HttpCookie cookie = exchange.getRequest().getCookies().getFirst(this.properties.getCookieName());
            System.out.println(cookie);
//            if (StrUtil.isBlank(token) || StrUtil.isBlank(type)) {
//                JSONObject message = new JSONObject();
//                message.put("code", StatusCodeConstants.TOKEN_NONE);
//                message.put("message", "鉴权失败，无token或类型");
//                byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);
//                DataBuffer buffer = response.bufferFactory().wrap(bits);
//                response.setStatusCode(HttpStatus.UNAUTHORIZED);
//                response.getHeaders().add("Content-Type", "text/json;charset=UTF-8");
//                return response.writeWith(Mono.just(buffer));
//                //有数据
//            }else {
//                String prefix = this.getPrefix(type);
//                //校验token
//                String userPhone = verifyJWT(token ,prefix);
//                if (StrUtil.isEmpty(userPhone)){
//                    JSONObject message = new JSONObject();
//                    message.put("message", "token错误");
//                    message.put("code", StatusCodeConstants.TOKEN_ERROR);
//                    byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);
//                    DataBuffer buffer = response.bufferFactory().wrap(bits);
//                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
//                    response.getHeaders().add("Content-Type", "text/json;charset=UTF-8");
//                    return response.writeWith(Mono.just(buffer));
//                }
//                //将现在的request，添加当前身份
//                ServerHttpRequest mutableReq = exchange.getRequest().mutate().header("Authorization-UserName", userPhone).build();
//                ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
//                return chain.filter(mutableExchange);
//            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}