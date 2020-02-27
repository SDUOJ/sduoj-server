/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName AuthApplication
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/27 13:01
 * @Version V1.0
 **/

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AuthApplication {    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}