/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.auth.controller;

import cn.edu.sdu.qd.oj.auth.config.JwtProperties;
import cn.edu.sdu.qd.oj.auth.entity.UserInfo;
import cn.edu.sdu.qd.oj.auth.service.AuthService;
import cn.edu.sdu.qd.oj.auth.utils.CookieBuilder;
import cn.edu.sdu.qd.oj.auth.utils.JwtUtils;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.utils.CookieUtils;
import cn.edu.sdu.qd.oj.user.pojo.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
    * @Description TODO
    * @param username
    * @param password
    * @return
    **/
    @PostMapping(value={"/auth/login", "/judger/auth/login", "/manage/auth/login"})
    @ResponseBody
    public ResponseEntity<ResponseResult<User>> login(RequestEntity<String> entity) {
        Map json = null;
        String username = null, password = null;

        try {
            json = objectMapper.readValue(entity.getBody(), Map.class);
            username = (String) json.get("username");
            password = (String) json.get("password");
        } catch (Exception ignore) {
        }

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
            HttpHeaders headers = new HttpHeaders();
            String cookie = new CookieBuilder().setKey(prop.getCookieName())
                            .setValue(token)
                            .setMaxAge(prop.getCookieMaxAge())
                            .setPath("/")
                            .build();
            headers.set("Set-Cookie", cookie);
            return new ResponseEntity<>(ResponseResult.ok(user), headers, HttpStatus.OK);
        } else {
            // 从Token中获取用户信息
            try {
                // TODO: 校验现有 cookie 超时与否
                HttpHeaders headers = entity.getHeaders();
                String token = headers.get("Cookie").get(0);
                token = token.replace(this.prop.getCookieName() + "=", "");
                UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
                User user = this.authService.queryUserById(userInfo.getUserId());
                return new ResponseEntity<>(ResponseResult.ok(user), HttpStatus.OK);
            } catch (Exception e) {
                throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
            }
        }
    }

    @GetMapping(value={"/auth/logout","/judger/auth/logout"})
    @ApiResponseBody
    public ResponseEntity<ResponseResult<Void>> logout() {
        String cookie = new CookieBuilder().setKey(prop.getCookieName())
                .setValue("")
                .setMaxAge(0)
                .setPath("/")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Set-Cookie", cookie);
        return new ResponseEntity<>(ResponseResult.ok(), headers, HttpStatus.OK);
    }
}