/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.auth.config;

import cn.edu.sdu.qd.oj.auth.utils.RsaUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @ClassName JwtProperties
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/27 14:13
 * @Version V1.0
 **/
@ConfigurationProperties(prefix = "sduoj.jwt")
@Getter
@Setter
public class JwtProperties {

    private String secret;        // 密钥

    private String pubKeyPath;    // 公钥

    private String priKeyPath;    // 私钥

    private int expire;          // token过期时间

    private PublicKey publicKey;  // 公钥

    private PrivateKey privateKey;// 私钥

    private String cookieName;    // cookie的name

    private Long cookieMaxAge; // cookie的存活时间

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    /**
     * @PostContruct：在构造方法执行之后执行该方法
     */
    @PostConstruct
    public void init() {
        try {
            File pubKey = new File(pubKeyPath);
            File priKey = new File(priKeyPath);
            if (!pubKey.exists() || !priKey.exists()) {
                // 生成公钥和私钥
                RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
            }
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            logger.error("[auth-service] 初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }
    }
}