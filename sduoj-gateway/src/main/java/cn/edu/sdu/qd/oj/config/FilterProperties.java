/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @ClassName FilterProperties
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/1 14:35
 * @Version V1.0
 **/

@ConfigurationProperties(prefix = "sduoj.filter")
@Getter
@Setter
public class FilterProperties {
    private List<String> allowPaths;
}