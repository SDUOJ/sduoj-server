package cn.edu.sdu.qd.oj.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sduoj.user")
public class UserServiceProperties {

    private String verificationUrlPrefix;
    private String forgetPasswordUrlPrefix;
    private int verificationExpire;
    private String fromEmail;
    private String verificationEmailSubject;
    private String forgetPasswordEmailSubject;
    private String resetEmailSubject;
}
