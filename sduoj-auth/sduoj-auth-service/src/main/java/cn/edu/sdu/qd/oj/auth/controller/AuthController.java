/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.auth.controller;

import cn.edu.sdu.qd.oj.auth.config.JwtProperties;
import cn.edu.sdu.qd.oj.auth.service.AuthService;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName AuthController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/27 14:17
 * @Version V1.0
 **/
@RestController
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
    * @return org.springframework.http.ResponseEntity<cn.edu.sdu.qd.oj.common.entity.ResponseResult>
    **/
    @PostMapping("accredit")
    public ResponseEntity<ResponseResult> authentication(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response) {
        // 登录校验
        String token = this.authService.authentication(username, password);
        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
        CookieUtils.setCookie(request, response, prop.getCookieName(),
                token, prop.getCookieMaxAge(), null, true);
        return ResponseEntity.ok().build();
    }
}