/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.user.service.UserService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiResponseBody
    public Void register(@Valid @RequestBody UserDTO userDTO) throws Exception {
        this.userService.register(userDTO);
        return null;
    }

    @GetMapping("/verifyEmail")
    @ApiResponseBody
    public Void emailVerify(@RequestParam("token") String token) {
        this.userService.emailVerify(token);
        return null;
    }

    @PostMapping("/sendVerificationEmail")
    @ApiResponseBody
    public String verificationEmailSend(@RequestBody Map<String, String> json) throws MessagingException {
        String username = json.get("username");
        return this.userService.sendVerificationEmail(username);
    }

    @PostMapping("/forgetPassword")
    @ApiResponseBody
    public String forgetPassword(@RequestBody Map<String, String> json) throws Exception {
        String username = null, email = null;
        try {
            username = json.get("username");
            email = json.get("email");
        }catch (Exception ignore) {
        }
        return this.userService.forgetPassword(username, email);
    }

    @PostMapping("/resetPassword")
    @ApiResponseBody
    public Void resetPassword(@RequestBody Map<String, String> json) {
        String token = json.get("token");
        String password = json.get("password");
        if (token == null || password == null) {
            throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
        }

        this.userService.resetPassword(token, password);
        return null;
    }

    @GetMapping("/getProfile")
    @ApiResponseBody
    public UserDTO getProfile(@RequestHeader("Authorization-userId") Long userId) {
        return this.userService.queryByUserId(userId);
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseResult<UserSessionDTO> login(HttpServletResponse response,
                                                @RequestBody @NotNull Map<String, String> json,
                                                @RequestHeader("X-FORWARDED-FOR") String ipv4,
                                                @RequestHeader("user-agent") String userAgent) throws ApiException {
        String username = null, password = null;
        try {
            username = json.get("username");
            password = json.get("password");
        } catch (Exception ignore) {
        }

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            // 登录校验
            UserSessionDTO userSessionDTO = this.userService.login(username, password, ipv4, userAgent);
            // TODO: 魔法值解决
            response.setHeader("SDUOJUserInfo", JSON.toJSONString(userSessionDTO));
            return ResponseResult.ok(userSessionDTO);
        }
        return ResponseResult.error();
    }

    @GetMapping("/logout")
    @ResponseBody
    public ResponseResult<Void> logout(HttpServletResponse response) {
        response.setHeader("SDUOJUserInfo", "Logout");
        return ResponseResult.ok(null);
    }
}
