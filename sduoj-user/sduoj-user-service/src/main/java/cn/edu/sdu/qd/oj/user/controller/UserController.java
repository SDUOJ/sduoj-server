/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.CaptchaUtils;
import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.user.dto.UserUpdateReqDTO;
import cn.edu.sdu.qd.oj.user.service.UserService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private RedisUtils redisUtils;

    @PostMapping("/register")
    @ApiResponseBody
    public Void register(@Valid @RequestBody UserDTO userDTO) throws Exception {
        verifyCaptcha(userDTO.getCaptchaId(), userDTO.getCaptcha());

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
        verifyCaptcha(json.get("captchaId"), json.get("captcha"));

        String username = json.get("username");
        return this.userService.sendVerificationEmail(username);
    }

    @PostMapping("/forgetPassword")
    @ApiResponseBody
    public String forgetPassword(@RequestBody Map<String, String> json) throws Exception {
        verifyCaptcha(json.get("captchaId"), json.get("captcha"));

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

    @PostMapping("/updateProfile")
    @ApiResponseBody
    public Void updateProfile(@RequestBody UserUpdateReqDTO reqDTO,
                              @RequestHeader("Authorization-userId") Long userId) throws MessagingException {
        // 新密码校验
        if (StringUtils.isNotBlank(reqDTO.getNewPassword())) {
            validatePassword(reqDTO.getNewPassword());
        }

        // 新邮箱校验
        if (StringUtils.isNotBlank(reqDTO.getNewEmail())) {
            validateEmail(reqDTO.getNewEmail());
        }

        reqDTO.setUserId(userId);
        this.userService.updateProfile(reqDTO);
        return null;
    }

    private void validateEmail(@Email(message = "邮箱不合法") String email) {
    }

    private void validatePassword(@Length(min = 4, max = 32, message = "密码长度必须在4-32位之间") String password) {

    }

    @GetMapping("/getCaptcha")
    @ApiResponseBody
    public Map<String, String> getCaptcha() {
        String uuid = UUID.randomUUID().toString();
        CaptchaUtils.CaptchaEntity captcha = CaptchaUtils.getRandomBase64Captcha();
        if (!redisUtils.set(RedisConstants.getCaptchaKey(uuid), captcha.getRandomStr(), RedisConstants.CAPTCHA_EXPIRE)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        Map<String, String> map = new HashMap<>();
        map.put("captcha", captcha.getBase64());
        map.put("captchaId", uuid);
        return map;
    }

    @GetMapping("/isExist")
    @ApiResponseBody
    public Boolean isExist(@RequestParam("username") @Nullable String username,
                           @RequestParam("email") @Nullable String email) {
        if (StringUtils.isNotBlank(username)) {
            return this.userService.isExistUsername(username);
        }
        if (StringUtils.isNotBlank(email)) {
            return this.userService.isExistEmail(email);
        }
        return false;
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


    /**
    * @Description 进行验证码验证
    * @exception  ApiException CAPTCHA_NOT_MATCHING
    * @exception  ApiException CAPTCHA_NOT_FOUND
    **/
    private void verifyCaptcha(String captchaId, String inputCaptcha) {
        if (captchaId == null || inputCaptcha == null) {
            throw new ApiException(ApiExceptionEnum.CAPTCHA_NOT_FOUND);
        }
        String captcha = Optional.ofNullable(redisUtils.get(RedisConstants.getCaptchaKey(captchaId))).map(o -> (String) o).orElse(null);
        if (captcha == null) {
            throw new ApiException(ApiExceptionEnum.CAPTCHA_NOT_FOUND);
        }
        if (!captcha.equalsIgnoreCase(inputCaptcha)) {
            throw new ApiException(ApiExceptionEnum.CAPTCHA_NOT_MATCHING);
        }
    }
}
