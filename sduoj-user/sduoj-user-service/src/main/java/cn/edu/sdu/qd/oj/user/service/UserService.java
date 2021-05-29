/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.service;

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.CaptchaUtils;
import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.common.util.RegexUtils;
import cn.edu.sdu.qd.oj.user.config.UserServiceProperties;
import cn.edu.sdu.qd.oj.user.converter.UserConverter;
import cn.edu.sdu.qd.oj.user.converter.UserSessionConverter;
import cn.edu.sdu.qd.oj.user.dao.UserDao;
import cn.edu.sdu.qd.oj.user.dao.UserSessionDao;
import cn.edu.sdu.qd.oj.user.dto.UserThirdPartyBindingReqDTO;
import cn.edu.sdu.qd.oj.user.dto.UserThirdPartyLoginRespDTO;
import cn.edu.sdu.qd.oj.user.dto.UserThirdPartyRegisterReqDTO;
import cn.edu.sdu.qd.oj.user.dto.UserUpdateReqDTO;
import cn.edu.sdu.qd.oj.user.entity.UserDO;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.entity.UserSessionDO;
import cn.edu.sdu.qd.oj.common.util.CodecUtils;
import cn.edu.sdu.qd.oj.user.enums.ThirdPartyEnum;
import cn.edu.sdu.qd.oj.user.sender.RabbitSender;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zhangt2333
 * @author zhaoyifan
 */

@Slf4j
@Service
@EnableConfigurationProperties({UserServiceProperties.class})
public class UserService {

    @Autowired
    private UserServiceProperties userServiceProperties;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSessionDao userSessionDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserSessionConverter userSessionConverter;

    @Autowired
    private RabbitSender rabbitSender;


    @Autowired
    private RestTemplate restTemplate;

    public UserDTO verify(String username, String password) throws ApiException {
        return userConverter.to(verifyAndGetDO(username, password));
    }

    /**
     * 校验用户账号密码并返回 DO
     */
    public @NotNull UserDO verifyAndGetDO(String username, String password) throws ApiException {
        // 查找对应用户后验证密码
        LambdaQueryChainWrapper<UserDO> query = userDao.lambdaQuery();
        if (username.indexOf('@') != -1) {
            query.eq(UserDO::getEmail, username);
        } else {
            query.eq(UserDO::getUsername, username);
        }
        UserDO userDO = query.one();
        AssertUtils.notNull(userDO, ApiExceptionEnum.USER_NOT_FOUND);
        AssertUtils.isTrue(userDO.getPassword().equals(CodecUtils.md5Hex(password, userDO.getSalt())), ApiExceptionEnum.PASSWORD_NOT_MATCHING);
        return userDO;
    }

    /**
    * @Description 登录
    * @exception ApiException PASSWORD_NOT_MATCHING
    * @return cn.edu.sdu.qd.oj.common.entity.UserSessionDTO
    **/
    @Transactional
    public UserSessionDTO login(String username, String password, String ipv4, String userAgent) throws ApiException {
        UserSessionDO userSessionDO = UserSessionDO.builder()
                .username(username)
                .ipv4(ipv4)
                .userAgent(userAgent)
                .success(0)
                .build();
        UserDO userDO = null;
        try {
            userDO = verifyAndGetDO(username, password);
            userSessionDO.setUsername(userDO.getUsername());
            userSessionDO.setSuccess(1);
            return userSessionConverter.to(userDO, userSessionDO);
        } catch (ApiException e) {
            throw e;
        } finally {
            userSessionDao.save(userSessionDO);
        }
    }

    @Transactional
    public UserSessionDTO register(UserDTO userDTO, String ipv4, String userAgent) {
        validateEmailCode(userDTO.getEmail(), userDTO.getEmailCode());

        UserDO userDO = userConverter.from(userDTO);
        userDO.setSalt(CodecUtils.generateSalt());
        userDO.setPassword(CodecUtils.md5Hex(userDO.getPassword(), userDO.getSalt()));
        userDO.setRoles(PermissionEnum.USER.name);
        if (StringUtils.isBlank(userDO.getNickname())) {
            userDO.setNickname(userDO.getUsername());
        }

        // TODO: username 重复时插入失败的异常处理器
        try {
            AssertUtils.isTrue(userDao.save(userDO), ApiExceptionEnum.UNKNOWN_ERROR);
        } catch (DuplicateKeyException e) {
            throw new ApiException(ApiExceptionEnum.USER_EXIST);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        consumeEmailCode(userDTO.getEmail(), userDTO.getEmailCode());
        return loginWithWritingSession(userDO, ipv4, userAgent);
    }

    public Map<Long, String> queryIdToUsernameMap() {
        List<UserDO> userDOList = userDao.lambdaQuery().select(UserDO::getUserId, UserDO::getUsername).list();
        return userDOList.stream().collect(Collectors.toMap(UserDO::getUserId, UserDO::getUsername, (k1, k2) -> k1));
    }

    public List<String> queryRolesById(Long userId) {
        UserDO userDO = userDao.lambdaQuery().select(UserDO::getRoles).eq(UserDO::getUserId, userId).one();
        return Optional.ofNullable(userDO).map(userConverter::to).map(UserDTO::getRoles).orElse(null);
    }

    public UserDTO queryByUserId(Long userId) {
        UserDO userDO = userDao.lambdaQuery().select(
            UserDO::getUserId,
            UserDO::getUsername,
            UserDO::getNickname,
            UserDO::getEmail,
            UserDO::getStudentId,
            UserDO::getPhone,
            UserDO::getGender,
            UserDO::getRoles,
            UserDO::getSduId
        ).eq(UserDO::getUserId, userId).one();
        AssertUtils.notNull(userDO, ApiExceptionEnum.USER_NOT_FOUND);
        return userConverter.to(userDO);
    }

    /**
     * 发送邮箱验证码到用户邮箱
     * @return send email interval
     */
    public Integer sendVerificationEmail(@Email(message = "parameter is not an email") String email) throws MessagingException {
        AssertUtils.isTrue(0 == userDao.lambdaQuery().eq(UserDO::getEmail, email).count(),
                ApiExceptionEnum.EMAIL_EXIST);
        // 特判邮件验证未开启
        if (!userServiceProperties.isEnableSendingEmailCode()) {
            throw new ApiException(ApiExceptionEnum.NONE_EMAIL_SENDER,
                    userServiceProperties.isEnableEmailVerification() ? " 请联系管理员" : " 邮件验证码或可以随便输入");
        }
        // 判断验证码是否未到期
        validateSendEmailInterval(email);
        // 邮箱验证码存在则沿用
        String redisKey = RedisConstants.getEmailCodeKey(email);
        String emailCode = Optional.ofNullable((String) redisUtils.get(redisKey)).orElseGet(() -> CaptchaUtils.getRandomString(6));
        // 发送邮件
        rabbitSender.sendEmailCode(email, emailCode);
        AssertUtils.isTrue(redisUtils.set(redisKey, emailCode, userServiceProperties.getVerificationExpire()),
                ApiExceptionEnum.UNKNOWN_ERROR);
        setSendEmailInterval(email);
        return userServiceProperties.getSendEmailInterval();
    }

    public String forgetPassword(String username, String email) throws Exception {
        UserDO userDO = null;
        if (username != null) {
            userDO = userDao.lambdaQuery().select(UserDO::getUsername, UserDO::getEmail).eq(UserDO::getUsername, username).one();
        }
        if (userDO == null && email != null) {
            userDO = userDao.lambdaQuery().select(UserDO::getUsername, UserDO::getEmail).eq(UserDO::getEmail, email).one();
        }
        AssertUtils.notNull(userDO, ApiExceptionEnum.USER_NOT_FOUND);
        email = userDO.getEmail();
        // 发送验证邮件
        String uuid = UUID.randomUUID().toString();
        redisUtils.set(RedisConstants.getForgetPasswordKey(uuid), userDO.getUsername(), userServiceProperties.getVerificationExpire());
        rabbitSender.sendForgetPasswordEmail(userDO.getUsername(), userDO.getEmail(), uuid);
        return email;
    }

    public void resetPassword(String token, String password) {
        String username = (String) redisUtils.get(RedisConstants.getForgetPasswordKey(token));
        AssertUtils.notNull(username, ApiExceptionEnum.TOKEN_EXPIRE);

        String salt = CodecUtils.generateSalt();
        String saltPassword = CodecUtils.md5Hex(password, salt);

        LambdaUpdateChainWrapper<UserDO> update = userDao.lambdaUpdate()
                .eq(UserDO::getUsername, username)
                .set(UserDO::getSalt, salt)
                .set(UserDO::getPassword, saltPassword);
        AssertUtils.isTrue(update.update(), ApiExceptionEnum.UNKNOWN_ERROR);
    }

    public boolean isExistUsername(String username) {
        return userDao.lambdaQuery().eq(UserDO::getUsername, username).count() > 0;
    }

    public boolean isExistEmail(String email) {
        return userDao.lambdaQuery().eq(UserDO::getEmail, email).count() > 0;
    }

    public void updateProfile(UserUpdateReqDTO reqDTO) {
        // 构造更新参数
        UserDO updateDTO = new UserDO();
        BeanUtils.copyProperties(reqDTO, updateDTO);

        // 更改密码逻辑
        if (StringUtils.isNotBlank(reqDTO.getNewPassword())) {
            validatePassword(reqDTO.getUserId(), reqDTO.getPassword());
            updateDTO.setPassword(reqDTO.getNewPassword());
            updateDTO.setSalt(CodecUtils.generateSalt());
            updateDTO.setPassword(CodecUtils.md5Hex(updateDTO.getPassword(), updateDTO.getSalt()));
        } else {
            updateDTO.setPassword(null);
        }

        userDao.updateById(updateDTO);
    }

    public void updateEmail(Long userId, String password, @Email(message = "parameter is not an email") @NotBlank String email, String emailCode) {
        validateEmailCode(email, emailCode);
        validatePassword(userId, password);
        // 特判用户标 UserFeatureEnum.BAN_EMAIL_UPDATE
        UserDO userDO = userDao.lambdaQuery().select(UserDO::getFeatures).eq(UserDO::getUserId, userId).one();
        if (userConverter.featuresTo(userDO.getFeatures()).isBanEmailUpdate()) {
            throw new ApiException(ApiExceptionEnum.FEATURE_ERROR, "该用户被禁止更改邮箱");
        }
        AssertUtils.isTrue(userDao.lambdaUpdate().set(UserDO::getEmail, email).eq(UserDO::getUserId, userId).update(),
                ApiExceptionEnum.UNKNOWN_ERROR);
        consumeEmailCode(email, emailCode);
    }

    /**
     * 校验账号密码
     * @exception ApiException ApiExceptionEnum.USER_NOT_FOUND
     * @exception ApiException ApiExceptionEnum.PASSWORD_NOT_MATCHING
     */
    private void validatePassword(Long userId, String password) {
        UserDO userDO = userDao.lambdaQuery().select(
                UserDO::getPassword,
                UserDO::getSalt
        ).eq(UserDO::getUserId, userId).one();
        AssertUtils.notNull(userDO, ApiExceptionEnum.USER_NOT_FOUND);
        AssertUtils.isTrue(CodecUtils.md5Hex(password, userDO.getSalt()).equals(userDO.getPassword()), ApiExceptionEnum.PASSWORD_NOT_MATCHING);
    }

    /**
     * 校验邮箱验证码，若邮件服务未开启则不校验
     * @exception ApiException ApiExceptionEnum.TOKEN_EXPIRE
     * @exception ApiException ApiExceptionEnum.CAPTCHA_NOT_MATCHING
     */
    private void validateEmailCode(String email, String emailCode) {
        // 验证邮箱码正确性, 注意此时未配置强制验证，则不验证邮箱码
        if (userServiceProperties.isEnableEmailVerification()) {
            String realEmailCode = (String) redisUtils.get(RedisConstants.getEmailCodeKey(email));
            AssertUtils.notNull(realEmailCode, ApiExceptionEnum.TOKEN_EXPIRE);
            AssertUtils.isTrue(StringUtils.equalsIgnoreCase(realEmailCode, emailCode), ApiExceptionEnum.CAPTCHA_NOT_MATCHING);
        }
    }

    /**
     * 核销邮箱验证码
     */
    private void consumeEmailCode(String email, String emailCode) {
        String realEmailCode = (String) redisUtils.get(RedisConstants.getEmailCodeKey(email));
        if (StringUtils.equalsIgnoreCase(emailCode, realEmailCode)) {
            redisUtils.del(RedisConstants.getEmailCodeKey(email));
        }
    }

    private void setSendEmailInterval(String email) {
        redisUtils.set(RedisConstants.getEmailIntervalKey(email), 0, userServiceProperties.getSendEmailInterval());
    }

    /**
     * 进行验证码验证
     * @throws ApiException ApiExceptionEnum.CAPTCHA_NOT_MATCHING
     * @throws ApiException ApiExceptionEnum.CAPTCHA_NOT_FOUND
     */
    public void verifyCaptcha(String captchaId, String inputCaptcha) {
        AssertUtils.notNull(captchaId, ApiExceptionEnum.CAPTCHA_NOT_FOUND);
        AssertUtils.notNull(inputCaptcha, ApiExceptionEnum.CAPTCHA_NOT_FOUND);

        String captcha = (String) redisUtils.get(RedisConstants.getCaptchaKey(captchaId));

        AssertUtils.notNull(captcha, ApiExceptionEnum.CAPTCHA_NOT_FOUND);
        AssertUtils.isTrue(captcha.equalsIgnoreCase(inputCaptcha), ApiExceptionEnum.CAPTCHA_NOT_MATCHING);
        redisUtils.del(RedisConstants.getCaptchaKey(captchaId));
    }

    public void validateSendEmailInterval(String email) {
        AssertUtils.isTrue(0 > redisUtils.getExpire(RedisConstants.getEmailIntervalKey(email)),
                ApiExceptionEnum.TOO_FREQUENT);
    }

    /**
     * 通过 SDU-CAS 服务器进行 validate 随后查询对应用户存在则登录返回
     */
    @Transactional
    public UserThirdPartyLoginRespDTO thirdPartyLoginBySduCas(String ticket, String ipv4, String userAgent) {
        AssertUtils.isTrue(userServiceProperties.isEnableThirdPartySduCas(), ApiExceptionEnum.THIRD_PARTY_ERROR,
                "SDUCAS 认证未打开");
        // validate by sdu cas server
        String service = userServiceProperties.getSduCasServiceUrl();
        String url = String.format("https://pass.sdu.edu.cn/cas/serviceValidate?ticket=%s&service=%s", ticket, service);
        String html = null;
        // retry 3 times
        for (int i = 0; i < 3; i++) {
            try {
                html = restTemplate.getForEntity(url, String.class).getBody();
            } catch (Exception e) {
                log.error("sducas login ticket:{} ERROR", ticket, e);
            }
            if (html != null && html.contains("cas")) {
                break;
            }
        }
        String sduId = RegexUtils.regexFind(html, "<cas:ID_NUMBER>(.*?)</cas:ID_NUMBER>");
        String realName = RegexUtils.regexFind(html, "<cas:USER_NAME>(.*?)</cas:USER_NAME>");
        log.info("ticket:{} ipv4:{} sduId:{}", ticket, ipv4, sduId);
        AssertUtils.isTrue(StringUtils.isNotBlank(sduId), ApiExceptionEnum.THIRD_PARTY_ERROR);
        UserThirdPartyLoginRespDTO respDTO = UserThirdPartyLoginRespDTO.builder()
                                                                       .sduId(sduId)
                                                                       .sduRealName(realName)
                                                                       .thirdParty(ThirdPartyEnum.SDUCAS)
                                                                       .build();
        // query db
        UserDO userDO = userDao.lambdaQuery().eq(UserDO::getSduId, sduId).one();
        if (userDO != null) {
            // 特判用户标 UserFeatureEnum.BAN_THIRD_PARTY
            if (userConverter.featuresTo(userDO.getFeatures()).isBanThirdParty()) {
                throw new ApiException(ApiExceptionEnum.FEATURE_ERROR, "该用户被禁止使用第三方认证");
            }
            respDTO.setUser(loginWithWritingSession(userDO, ipv4, userAgent));
            // 临时代码，每次登录后把 nickname 改成 realName
            userDao.lambdaUpdate()
                   .set(UserDO::getNickname, realName)
                   .set(UserDO::getStudentId, sduId)
                   .eq(UserDO::getUserId, userDO.getUserId())
                   .update();
            userDO.setNickname(realName);
        } else {
            // 缓存到 redis 以备后续操作
            String uuid = UUID.randomUUID().toString();
            respDTO.setToken(uuid);
            redisUtils.set(RedisConstants.getThirdPartyToken(uuid), JSON.toJSONString(respDTO), RedisConstants.SDU_CAS_EXPIRE);
        }
        return respDTO;
    }

    /**
     * 处理第三方登录自动创建
     */
    @Transactional
    public UserSessionDTO thirdPartyRegister(UserThirdPartyRegisterReqDTO reqDTO, String ipv4, String userAgent) {
        // 验证邮箱存在性
        AssertUtils.isTrue(!isExistUsername(reqDTO.getEmail()), ApiExceptionEnum.EMAIL_EXIST);
        // 取 redis 中的 token
        UserThirdPartyLoginRespDTO respDTO = JSON.parseObject((String) redisUtils.get(RedisConstants.getThirdPartyToken(reqDTO.getToken())),
                UserThirdPartyLoginRespDTO.class);
        AssertUtils.notNull(respDTO, ApiExceptionEnum.THIRD_PARTY_NOT_EXIST);
        // 验证 emailCode
        validateEmailCode(reqDTO.getEmail(), reqDTO.getEmailCode());
        // 验证用户名存在性
        AssertUtils.isTrue(!isExistUsername(reqDTO.getUsername()), ApiExceptionEnum.USER_EXIST);
        // 创建账号方式
        UserDO userDO = UserDO.builder()
                              .username(reqDTO.getUsername())
                              .email(reqDTO.getEmail())
                              .password(reqDTO.getPassword())
                              .roles(PermissionEnum.USER.name)
                              .build();
        switch (respDTO.getThirdParty()) {
            case SDUCAS:
                thirdPartyRegisterBySduCas(userDO, respDTO);
                break;
            case QQ:
            case WECHAT:
                throw new ApiException(ApiExceptionEnum.THIRD_PARTY_ERROR, "暂不支持这种第三方认证");
        }
        try {
            AssertUtils.isTrue(userDao.save(userDO), ApiExceptionEnum.UNKNOWN_ERROR);
        } catch (DuplicateKeyException e) {
            log.error("{}", respDTO, e);
            throw new ApiException(ApiExceptionEnum.USER_EXIST);
        } catch (Exception e) {
            log.error("{}", respDTO, e);
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        // 账户生成成功, 删除 redis 中对应的 token
        redisUtils.del(RedisConstants.getThirdPartyToken(reqDTO.getToken()));
        consumeEmailCode(reqDTO.getEmail(), reqDTO.getEmailCode());
        // 登录
        return loginWithWritingSession(userDO, ipv4, userAgent);
    }

    /**
     * 处理第三方登录创建账号的 userDO (sducas)
     */
    private void thirdPartyRegisterBySduCas(UserDO userDO, UserThirdPartyLoginRespDTO tokenDTO) {
        // 取 redis 中的 token
        AssertUtils.isTrue(null == userDao.lambdaQuery().eq(UserDO::getSduId, tokenDTO.getSduId()).one(),
                ApiExceptionEnum.USER_EXIST);
        // 生成用户
        userDO.setSduId(tokenDTO.getSduId());
        userDO.setStudentId(tokenDTO.getSduId());
        userDO.setNickname(tokenDTO.getSduRealName());
        userDO.setSalt(CodecUtils.generateSalt());
        userDO.setPassword(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        userDO.setPassword(CodecUtils.md5Hex(userDO.getPassword(), userDO.getSalt()));
    }


    /**
     * 第三方登录绑定已有账号
     */
    @Transactional
    public UserSessionDTO thirdPartyBinding(UserThirdPartyBindingReqDTO reqDTO, String ipv4, String userAgent) {
        // 取 redis 中的 token
        UserThirdPartyLoginRespDTO respDTO = JSON.parseObject((String) redisUtils.get(RedisConstants.getThirdPartyToken(reqDTO.getToken())),
                UserThirdPartyLoginRespDTO.class);
        AssertUtils.notNull(respDTO, ApiExceptionEnum.THIRD_PARTY_NOT_EXIST);
        ThirdPartyEnum thirdParty = respDTO.getThirdParty();
        // 调用具体的第三方绑定方式
        UserDO userDO = null;
        switch (thirdParty) {
            case SDUCAS:
                userDO = thirdPartyBindingBySduCas(reqDTO, respDTO);
                break;
            case QQ:
            case WECHAT:
                throw new ApiException(ApiExceptionEnum.THIRD_PARTY_ERROR, "暂不支持这种第三方认证");
        }
        // 特判用户标 UserFeatureEnum.BAN_THIRD_PARTY
        if (userConverter.featuresTo(userDO.getFeatures()).isBanThirdParty()) {
            throw new ApiException(ApiExceptionEnum.FEATURE_ERROR, "该用户被禁止使用第三方认证");
        }
        // 删除 redis 中对应的 token
        redisUtils.del(RedisConstants.getThirdPartyToken(reqDTO.getToken()));
        // 登录
        return loginWithWritingSession(userDO, ipv4, userAgent);
    }

    /**
     * 校验并第三方绑定已有账号
     */
    private UserDO thirdPartyBindingBySduCas(UserThirdPartyBindingReqDTO reqDTO, UserThirdPartyLoginRespDTO tokenDTO) {
        // 取账号并且校验
        UserDO userDO = userDao.lambdaQuery().eq(UserDO::getSduId, tokenDTO.getSduId()).one();
        AssertUtils.isTrue(userDO == null, ApiExceptionEnum.USER_EXIST);
        userDO = verifyAndGetDO(reqDTO.getUsername(), reqDTO.getPassword());
        AssertUtils.isTrue(StringUtils.isBlank(userDO.getSduId()), ApiExceptionEnum.THIRD_PARTY_BOUND);
        // 更新绑定关系
        AssertUtils.isTrue(userDao.lambdaUpdate().eq(UserDO::getUserId, userDO.getUserId())
                                  .set(UserDO::getSduId, tokenDTO.getSduId())
                                  .update(), ApiExceptionEnum.THIRD_PARTY_ERROR);
        userDO.setSduId(tokenDTO.getSduId());
        return userDO;
    }

    /**
     * 登录并写 session
     */
    private UserSessionDTO loginWithWritingSession(UserDO userDO, String ipv4, String userAgent) {
        UserSessionDO userSessionDO = UserSessionDO.builder()
                                                   .username(userDO.getUsername())
                                                   .ipv4(ipv4)
                                                   .userAgent(userAgent)
                                                   .success(1)
                                                   .build();
        userSessionDao.save(userSessionDO);
        return userSessionConverter.to(userDO, userSessionDO);
    }

    public void thirdPartyUnbinding(ThirdPartyEnum thirdParty, UserSessionDTO userSessionDTO) {
        switch (thirdParty) {
            case SDUCAS:
                AssertUtils.isTrue(userDao.lambdaUpdate()
                                          .set(UserDO::getSduId, null)
                                          .eq(UserDO::getUserId, userSessionDTO.getUserId())
                                          .update(), ApiExceptionEnum.THIRD_PARTY_ERROR);
                break;
            case QQ:
            case WECHAT:
                throw new ApiException(ApiExceptionEnum.THIRD_PARTY_ERROR, "暂不支持这种第三方认证");
        }
    }

    public Long usernameToUserId(String username) {
        return userDao.getBaseMapper().usernameToUserId(username);
    }

    public String userIdToUsername(Long userId) {
        return userDao.getBaseMapper().userIdToUsername(userId);
    }

    public String userIdToNickname(Long userId) {
        return userDao.getBaseMapper().userIdToNickname(userId);
    }
}