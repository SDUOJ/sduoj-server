/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.auth.controller;

import cn.edu.sdu.qd.oj.auth.config.JwtProperties;
import cn.edu.sdu.qd.oj.auth.entity.UserInfo;
import cn.edu.sdu.qd.oj.auth.service.AuthService;
import cn.edu.sdu.qd.oj.auth.utils.JwtUtils;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.utils.CookieUtils;
import cn.edu.sdu.qd.oj.user.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName AuthController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/27 14:17
 * @Version V1.0
 **/
@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties prop;

    /**
    * @Description TODO
    * @param username
    * @param password
    * @return
    **/
    @PostMapping("login")
    @ApiResponseBody
    public User authentication(
            @RequestBody Map json,
            HttpServletRequest request,
            HttpServletResponse response) {
        String username = (String) json.get("username");
        String password = (String) json.get("password");
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            // 登录校验
            User user = this.authService.authentication(username, password);
            if (user == null) {
                throw new ApiException(ApiExceptionEnum.PASSWORD_NOT_MATCHING);
            }
            // 计算token
            String token = null;
            try {
                token = JwtUtils.generateToken(new UserInfo(user.getUserId(), user.getUsername()),
                                                  prop.getPrivateKey(), prop.getExpire());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
            CookieUtils.newBuilder(response)
                       .httpOnly()
                       .maxAge(prop.getCookieMaxAge())
                       .request(request).build(prop.getCookieName(), token);
            return user;
        } else {
            String token = CookieUtils.getCookieValue(request, this.prop.getCookieName());
            // TODO: 校验现有 cookie 超时与否
            //从Token中获取用户信息
            try {
                UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
                User user = this.authService.queryUserById(userInfo.getUserId());
                return user;
            } catch (Exception e) {
                throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
            }
        }
    }

    @GetMapping("logout")
    @ApiResponseBody
    public Void logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        CookieUtils.newBuilder(response)
                   .httpOnly()
                   .maxAge(0)
                   .request(request)
                   .build(prop.getCookieName(), null);
        return null;
    }
}