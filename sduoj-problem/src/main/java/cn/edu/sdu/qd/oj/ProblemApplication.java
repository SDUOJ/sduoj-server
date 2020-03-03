package cn.edu.sdu.qd.oj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = {"cn.edu.sdu.qd.oj.problem.mapper"})
public class ProblemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProblemApplication.class,args);
    }
}
