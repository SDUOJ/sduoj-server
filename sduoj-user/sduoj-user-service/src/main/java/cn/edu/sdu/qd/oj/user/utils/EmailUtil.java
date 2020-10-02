package cn.edu.sdu.qd.oj.user.utils;

import cn.edu.sdu.qd.oj.user.config.UserServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Email;

@Component
@Slf4j
@EnableConfigurationProperties({UserServiceProperties.class})
public class EmailUtil {

    @Autowired
    private UserServiceProperties userServiceProperties;

    @Autowired
    private JavaMailSender javaMailSender;

    static final String EMAIL_VERIFY_PATTERN =
                    "<html>" +
                    "  <body>" +
                    "  <h2>Dear %s, </h2>" +
                    "  <br>" +
                    "  请点击以下链接对你的账号进行验证" +
                    "  <br>" +
                    "  <div style=\"padding: 12px; background-color: #E0E0E0; color: #000000; font-weight: bold; font-size: 16px;\" align=\"center\">" +
                    "      <a href=\"%s\">%s</a>" +
                    "  </div>" +
                    "<div style=\"text-align: center;padding: 50px 50px 24px;color: #515a6e;font-size: 14px;\">2020-2020 &copy; Shandong University</div>" +
                    "  </body>" +
                    "</html>";

    static final String FORGET_PASSWORD_PATTERN =
            "<html>" +
                    "  <body>" +
                    "  <h2>Dear %s, </h2>" +
                    "  <br>" +
                    "  请点击以下链接对你的账号进行重置密码" +
                    "  <br>" +
                    "  <div style=\"padding: 12px; background-color: #E0E0E0; color: #000000; font-weight: bold; font-size: 16px;\" align=\"center\">" +
                    "      <a href=\"%s\">%s</a>" +
                    "  </div>" +
                    "<div style=\"text-align: center;padding: 50px 50px 24px;color: #515a6e;font-size: 14px;\">2020-2020 &copy; Shandong University</div>" +
                    "  </body>" +
                    "</html>";

    static final String RESET_EMAIL_PATTERN =
            "<html>" +
                    "  <body>" +
                    "  <h2>Dear %s, </h2>" +
                    "  <br>" +
                    "  请点击以下链接对你的账号进行重设邮箱验证" +
                    "  <br>" +
                    "  <div style=\"padding: 12px; background-color: #E0E0E0; color: #000000; font-weight: bold; font-size: 16px;\" align=\"center\">" +
                    "      <a href=\"%s\">%s</a>" +
                    "  </div>" +
                    "<div style=\"text-align: center;padding: 50px 50px 24px;color: #515a6e;font-size: 14px;\">2020-2020 &copy; Shandong University</div>" +
                    "  </body>" +
                    "</html>";

    public void sendResetEmailMail(String username, @Email String email, String token) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setSubject(userServiceProperties.getResetEmailSubject());
        helper.setTo(email);
        helper.setFrom(userServiceProperties.getFromEmail());
        String url = userServiceProperties.getVerificationUrlPrefix() + token;
        helper.setText(String.format(RESET_EMAIL_PATTERN, username, url, url), true);
        javaMailSender.send(mail);
    }

    public void sendVerificationEmail(String username, @Email String email, String token) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setSubject(userServiceProperties.getVerificationEmailSubject());
        helper.setTo(email);
        helper.setFrom(userServiceProperties.getFromEmail());
        String url = userServiceProperties.getVerificationUrlPrefix() +token;
        helper.setText(String.format(EMAIL_VERIFY_PATTERN, username, url, url), true);
        javaMailSender.send(mail);
    }

    public void sendForgetPasswordEmail(String username, @Email String email, String token) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setSubject(userServiceProperties.getForgetPasswordEmailSubject());
        helper.setTo(email);
        helper.setFrom(userServiceProperties.getFromEmail());
        String url = userServiceProperties.getForgetPasswordUrlPrefix() +token;
        helper.setText(String.format(FORGET_PASSWORD_PATTERN, username, url, url), true);
        javaMailSender.send(mail);
    }
}
