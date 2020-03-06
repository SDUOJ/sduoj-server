/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName SubmitApplication
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/6 15:34
 * @Version V1.0
 **/

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = {"cn.edu.sdu.qd.oj.submit.mapper"})
public class SubmitApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubmitApplication.class, args);
    }
}