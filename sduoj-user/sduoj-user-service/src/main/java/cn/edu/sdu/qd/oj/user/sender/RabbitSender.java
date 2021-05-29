package cn.edu.sdu.qd.oj.user.sender;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.email.dto.EmailMessageDTO;
import cn.edu.sdu.qd.oj.user.config.UserServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;

/**
 * 封装一层工具层，用于发送 mq 消息
 *
 * @author zhangt2333
 */
@Slf4j
@Component
@EnableConfigurationProperties({UserServiceProperties.class})
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserServiceProperties userServiceProperties;


    public void sendEmailCode(@Email String email, String emailCode) {
        String subject = userServiceProperties.getVerifyEmailSubject();
        String text = String.format(userServiceProperties.getVerifyEmailPattern(), emailCode);
        EmailMessageDTO messageDTO = EmailMessageDTO.builder()
                                                    .to(email)
                                                    .subject(subject)
                                                    .text(text)
                                                    .build();
        AssertUtils.isTrue(sendEmail(messageDTO), ApiExceptionEnum.UNKNOWN_ERROR, "MQ Error");
    }

    public void sendForgetPasswordEmail(String username, @Email String email, String token) {
        String subject = userServiceProperties.getForgetPasswordEmailSubject();
        String url = userServiceProperties.getForgetPasswordUrlPrefix() + token;
        String text = String.format(userServiceProperties.getForgetPasswordPattern(), username, url, url);
        EmailMessageDTO messageDTO = EmailMessageDTO.builder()
                                                    .to(email)
                                                    .subject(subject)
                                                    .text(text)
                                                    .build();
        AssertUtils.isTrue(sendEmail(messageDTO), ApiExceptionEnum.UNKNOWN_ERROR, "MQ Error");
    }


    public boolean sendEmail(EmailMessageDTO messageDTO) {
        return send("sduoj.email", "send", messageDTO);
    }


    private boolean send(String exchange, String routingKey, Object o) {
        for (int i = 0; i < 5; i++) {
            try {
                this.rabbitTemplate.convertAndSend(exchange, routingKey, o);
                return true;
            } catch (AmqpException e) {
                log.warn("send", e);
                try {
                    Thread.sleep(i * 2000L);
                } catch (Throwable ignore) {
                }
            } catch (Exception e) {
                log.error("send", e);
                return false;
            }
        }
        return false;
    }
}
