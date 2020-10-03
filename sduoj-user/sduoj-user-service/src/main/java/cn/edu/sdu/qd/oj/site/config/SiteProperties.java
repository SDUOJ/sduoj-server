package cn.edu.sdu.qd.oj.site.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sduoj.site")
public class SiteProperties {

    private String copyright;

}
