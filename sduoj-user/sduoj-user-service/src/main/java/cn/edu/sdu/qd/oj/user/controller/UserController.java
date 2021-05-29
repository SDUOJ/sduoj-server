/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.annotation.RealIp;
import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.CaptchaUtils;
import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.user.dto.UserThirdPartyBindingReqDTO;
import cn.edu.sdu.qd.oj.user.dto.UserThirdPartyLoginRespDTO;
import cn.edu.sdu.qd.oj.user.dto.UserThirdPartyRegisterReqDTO;
import cn.edu.sdu.qd.oj.user.dto.UserUpdateReqDTO;
import cn.edu.sdu.qd.oj.user.enums.ThirdPartyEnum;
import cn.edu.sdu.qd.oj.user.service.UserExtensionService;
import cn.edu.sdu.qd.oj.user.service.UserService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author zhangt2333
 * @author zhaoyifan
 */

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserExtensionService userExtensionService;

    @Autowired
    private RedisUtils redisUtils;

    @PostMapping("/register")
    @ResponseBody
    public ResponseResult<UserSessionDTO> register(HttpServletResponse response,
                                                   @Valid @RequestBody UserDTO userDTO,
                                                   @RealIp String ipv4,
                                                   @RequestHeader("user-agent") String userAgent) {
        userDTO.setRoles(null);
        UserSessionDTO userSessionDTO = this.userService.register(userDTO, ipv4, userAgent);
        writeSessionToHeader(response, userSessionDTO);
        return ResponseResult.ok(userSessionDTO);
    }


    /**
     * 发送验证码到邮箱
     * @return send email interval
     */
    @PostMapping("/sendVerificationEmail")
    @ApiResponseBody
    public Integer verificationEmailSend(@RequestBody Map<String, String> json) throws MessagingException {
        userService.verifyCaptcha(json.get("captchaId"), json.get("captcha"));
        return this.userService.sendVerificationEmail(json.get("email"));
    }

    @PostMapping("/forgetPassword")
    @ApiResponseBody
    public String forgetPassword(@RequestBody Map<String, String> json) throws Exception {
        userService.verifyCaptcha(json.get("captchaId"), json.get("captcha"));
        return this.userService.forgetPassword(json.get("username"), json.get("email"));
    }

    @PostMapping("/resetPassword")
    @ApiResponseBody
    public Void resetPassword(@RequestBody Map<String, String> json) {
        String token = json.get("token");
        String password = json.get("password");
        AssertUtils.notNull(token, ApiExceptionEnum.PARAMETER_ERROR);
        AssertUtils.notNull(password, ApiExceptionEnum.PARAMETER_ERROR);
        this.userService.resetPassword(token, password);
        return null;
    }

    @GetMapping("/getProfile")
    @ApiResponseBody
    public UserDTO getProfile(@UserSession UserSessionDTO userSessionDTO) {
        return this.userService.queryByUserId(userSessionDTO.getUserId());
    }

    @PostMapping("/updateProfile")
    @ApiResponseBody
    public Void updateProfile(@RequestBody UserUpdateReqDTO reqDTO,
                              @UserSession UserSessionDTO userSessionDTO) {
        // 新密码格式校验
        if (StringUtils.isNotBlank(reqDTO.getNewPassword())) {
            validatePasswordFormat(reqDTO.getNewPassword());
        }
        reqDTO.setUserId(userSessionDTO.getUserId());
        this.userService.updateProfile(reqDTO);
        return null;
    }

    private void validatePasswordFormat(@Length(min = 4, max = 32, message = "密码长度必须在4-32位之间") String password) {
    }

    @PostMapping("/updateEmail")
    @ApiResponseBody
    public Void updateEmail(@RequestBody Map<String, String> json,
                            @UserSession UserSessionDTO userSessionDTO) {
        String password = json.get("password");
        String email = json.get("email");
        String emailCode = json.get("emailCode");
        this.userService.updateEmail(userSessionDTO.getUserId(), password, email, emailCode);
        return null;
    }

    @GetMapping("/getCaptcha")
    @ApiResponseBody
    public Map<String, String> getCaptcha() {
        String uuid = UUID.randomUUID().toString();
        CaptchaUtils.CaptchaEntity captcha = CaptchaUtils.getRandomBase64Captcha();
        AssertUtils.isTrue(redisUtils.set(RedisConstants.getCaptchaKey(uuid), captcha.getRandomStr(), RedisConstants.CAPTCHA_EXPIRE), ApiExceptionEnum.UNKNOWN_ERROR);
        Map<String, String> map = new HashMap<>();
        map.put("captcha", captcha.getBase64());
        map.put("captchaId", uuid);
        return map;
    }

    @GetMapping("/isExist")
    @ApiResponseBody
    public Boolean isExist(@RequestParam(value = "username", required = false) String username,
                           @RequestParam(value = "email", required = false) String email) {
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
                                                @RealIp String ipv4,
                                                @RequestHeader("user-agent") String userAgent) throws ApiException {

        String username = json.get("username");
        String password = json.get("password");
        log.info("{} login from {} by {}", username, ipv4, userAgent);
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            // 登录校验
            UserSessionDTO userSessionDTO = this.userService.login(username, password, ipv4, userAgent);
            writeSessionToHeader(response, userSessionDTO);
            return ResponseResult.ok(userSessionDTO);
        }
        return ResponseResult.error();
    }

    @GetMapping("/thirdPartyLogin")
    @ResponseBody
    public ResponseResult<UserThirdPartyLoginRespDTO> thirdPartyLogin(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      @RealIp String ipv4,
                                                                      @RequestHeader("user-agent") String userAgent) {
        ThirdPartyEnum thirdParty = ThirdPartyEnum.of(request.getParameter("thirdParty"));
        AssertUtils.notNull(thirdParty, ApiExceptionEnum.THIRD_PARTY_NOT_EXIST);

        UserThirdPartyLoginRespDTO respDTO = null;
        switch (thirdParty) {
            case SDUCAS:
                respDTO = userService.thirdPartyLoginBySduCas(request.getParameter("ticket"), ipv4, userAgent);
                break;
            case QQ:
            case WECHAT:
                throw new ApiException(ApiExceptionEnum.THIRD_PARTY_ERROR, "暂不支持这种第三方认证");
        }

        Optional.ofNullable(respDTO).map(UserThirdPartyLoginRespDTO::getUser).ifPresent(userSessionDTO -> {
            writeSessionToHeader(response, userSessionDTO);
        });

        return respDTO != null ? ResponseResult.ok(respDTO) : ResponseResult.error();
    }

    @PostMapping("/thirdPartyRegister")
    @ResponseBody
    public ResponseResult<UserSessionDTO> thirdPartyRegister(@RequestBody UserThirdPartyRegisterReqDTO reqDTO,
                                                             HttpServletResponse response,
                                                             @RealIp String ipv4,
                                                             @RequestHeader("user-agent") String userAgent) {
        UserSessionDTO userSessionDTO = userService.thirdPartyRegister(reqDTO, ipv4, userAgent);
        writeSessionToHeader(response, userSessionDTO);
        return ResponseResult.ok(userSessionDTO);
    }

    @PostMapping("/thirdPartyBinding")
    @ResponseBody
    public ResponseResult<UserSessionDTO> thirdPartyBinding(@RequestBody UserThirdPartyBindingReqDTO reqDTO,
                                                            HttpServletResponse response,
                                                            @RealIp String ipv4,
                                                            @RequestHeader("user-agent") String userAgent) {
        UserSessionDTO userSessionDTO = userService.thirdPartyBinding(reqDTO, ipv4, userAgent);
        writeSessionToHeader(response, userSessionDTO);
        return ResponseResult.ok(userSessionDTO);
    }

    @GetMapping("/thirdPartyUnbinding")
    @ApiResponseBody
    public Void thirdPartyUnbinding(@RequestParam("thirdParty") String thirdPartyStr,
                                    @UserSession UserSessionDTO userSessionDTO) {
        ThirdPartyEnum thirdParty = ThirdPartyEnum.of(thirdPartyStr);
        AssertUtils.notNull(thirdParty, ApiExceptionEnum.THIRD_PARTY_NOT_EXIST);
        userService.thirdPartyUnbinding(thirdParty, userSessionDTO);
        return null;
    }

    @GetMapping("/logout")
    @ResponseBody
    public ResponseResult<Void> logout(HttpServletResponse response) {
        response.setHeader(UserSessionDTO.HEADER_KEY, UserSessionDTO.HEADER_VALUE_LOGOUT);
        return ResponseResult.ok(null);
    }


    /**
     * 查询用户参加过的比赛
     */
    @GetMapping("/queryParticipateContest")
    @ApiResponseBody
    public List<Long> queryParticipateContest(@UserSession UserSessionDTO userSessionDTO) {
        return userExtensionService.queryParticipateContest(userSessionDTO.getUserId());
    }

    /**
     * 将 session 写入 header 与 gateway 配合做用户登录
     */
    private void writeSessionToHeader(@NotNull HttpServletResponse response,
                                      @Nullable UserSessionDTO userSessionDTO) {
        if (userSessionDTO != null) {
            response.setHeader(UserSessionDTO.HEADER_KEY, JSON.toJSONString(userSessionDTO));
        }
    }
}