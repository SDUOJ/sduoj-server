/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @ClassName GlobalCorsConfig
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/1 14:16
 * @Version V1.0
 **/

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        // 添加CORS的配置信息
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域, 不能写*, 否则cookie就不能用了
        config.addAllowedOrigin("http://manage.oj.oops-sdu.cn");
        config.addAllowedOrigin("http://oj.oops-sdu.cn");
        config.addAllowedOrigin("http://oj.oops-sdu.cn:8080");
        config.addAllowedOrigin("http://oj.xrvitd.com");
        config.addAllowedOrigin("http://oj.xrvitd.com:8080");
        // 是否发送cookie信息
        config.setAllowCredentials(true);
        // 允许的请求方式
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        // 允许的头信息
        config.addAllowedHeader("*");
        // 添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        // 返回新的CorsFilter.
        return new CorsFilter(configSource);
    }
}
