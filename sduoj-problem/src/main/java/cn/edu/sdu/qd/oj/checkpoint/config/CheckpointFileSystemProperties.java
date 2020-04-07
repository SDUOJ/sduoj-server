/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName CheckpointFileSystemProperties
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/6 16:34
 * @Version V1.0
 **/

@ConfigurationProperties(prefix = "sduoj.checkpoint")
@Getter
@Setter
public class CheckpointFileSystemProperties {
    private String baseDir; // 工作目录
}