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
import cn.edu.sdu.qd.oj.user.utils.EmailUtil;
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
    private EmailUtil emailUtil;


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
    public void register(UserDTO userDTO) throws Exception {
        UserDO userDO = userConverter.from(userDTO);
        userDO.setSalt(CodecUtils.generateSalt());
        userDO.setPassword(CodecUtils.md5Hex(userDO.getPassword(), userDO.getSalt()));
        if (StringUtils.isBlank(userDO.getNickname())) {
            userDO.setNickname(userDO.getUsername());
        }

        if (!emailUtil.isEmailEnable()) {
            userDO.setEmailVerified(1);
            userDO.setRoles(PermissionEnum.USER.name);
        }

        // TODO: username 重复时插入失败的异常处理器
        try {
            AssertUtils.isTrue(userDao.save(userDO), ApiExceptionEnum.UNKNOWN_ERROR);
        } catch (DuplicateKeyException e) {
            throw new ApiException(ApiExceptionEnum.USER_EXIST);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }


        if (emailUtil.isEmailEnable()) {
            sendVerificationEmail(userDTO.getUsername(), userDTO.getEmail());
        }
    }

    public Map<Long, String> queryIdToUsernameMap() {
        List<UserDO> userDOList = userDao.lambdaQuery().select(UserDO::getUserId, UserDO::getUsername).list();
        return userDOList.stream().collect(Collectors.toMap(UserDO::getUserId, UserDO::getUsername, (k1, k2) -> k1));
    }

    public List<String> queryRolesById(Long userId) {
        UserDO userDO = userDao.lambdaQuery().select(UserDO::getRoles).eq(UserDO::getUserId, userId).one();
        return Optional.ofNullable(userDO).map(userConverter::to).map(UserDTO::getRoles).orElse(null);
    }

    public void emailVerify(String token) {
        String username = (String) Optional.ofNullable(redisUtils.get(RedisConstants.getEmailVerificationKey(token))).orElse(null);
        AssertUtils.notNull(username, ApiExceptionEnum.TOKEN_EXPIRE);
        UserDO userDO = userDao.lambdaQuery().eq(UserDO::getUsername, username).select(
                UserDO::getUserId,
                UserDO::getVersion,
                UserDO::getRoles
        ).one();
        userDO.setEmailVerified(1);
        // 有角色时不更新 roles, 无时更新为 USER
        userDO.setRoles(StringUtils.isNotBlank(userDO.getRoles()) ? null : PermissionEnum.USER.name);
        AssertUtils.isTrue(userDao.updateById(userDO), ApiExceptionEnum.UNKNOWN_ERROR);
    }

    public UserDTO queryByUserId(Long userId) {
        UserDO userDO = userDao.lambdaQuery().select(
            UserDO::getUserId,
            UserDO::getUsername,
            UserDO::getNickname,
            UserDO::getEmail,
            UserDO::getEmailVerified,
            UserDO::getStudentId,
            UserDO::getPhone,
            UserDO::getGender,
            UserDO::getRoles,
            UserDO::getSduId
        ).eq(UserDO::getUserId, userId).one();
        AssertUtils.notNull(userDO, ApiExceptionEnum.USER_NOT_FOUND);
        return userConverter.to(userDO);
    }

    public void sendVerificationEmail(String username, String email) throws MessagingException {
        // 发送验证邮件
        String uuid = UUID.randomUUID().toString();
        redisUtils.set(RedisConstants.getEmailVerificationKey(uuid), username, userServiceProperties.getVerificationExpire());
        emailUtil.sendVerificationEmail(username, email, uuid);
    }

    public String sendVerificationEmail(String username) throws MessagingException {
        UserDO userDO = userDao.lambdaQuery().select(UserDO::getEmail).eq(UserDO::getUsername, username).one();
        AssertUtils.notNull(userDO, ApiExceptionEnum.USER_NOT_FOUND);
        sendVerificationEmail(username, userDO.getEmail());
        return userDO.getEmail();
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
        emailUtil.sendForgetPasswordEmail(userDO.getUsername(), userDO.getEmail(), uuid);
        return email;
    }

    public void resetPassword(String token, String password) {
        String username = Optional.ofNullable(redisUtils.get(RedisConstants.getForgetPasswordKey(token))).map(o -> (String) o).orElse(null);
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

    @Transactional
    public void updateProfile(UserUpdateReqDTO reqDTO) throws MessagingException {
        // 校验密码逻辑
        UserDO userDO = userDao.lambdaQuery().select(
                UserDO::getPassword,
                UserDO::getUsername,
                UserDO::getSalt
        ).eq(UserDO::getUserId, reqDTO.getUserId()).one();
        AssertUtils.notNull(userDO, ApiExceptionEnum.USER_NOT_FOUND);
        AssertUtils.isTrue(CodecUtils.md5Hex(reqDTO.getPassword(), userDO.getSalt()).equals(userDO.getPassword()), ApiExceptionEnum.PASSWORD_NOT_MATCHING);

        // 构造更新参数
        reqDTO.setPassword(null);
        UserDO updateDTO = new UserDO();
        BeanUtils.copyProperties(reqDTO, updateDTO);

        // 更改密码逻辑
        if (StringUtils.isNotBlank(reqDTO.getNewPassword())) {
            updateDTO.setPassword(reqDTO.getNewPassword());
            updateDTO.setSalt(CodecUtils.generateSalt());
            updateDTO.setPassword(CodecUtils.md5Hex(updateDTO.getPassword(), updateDTO.getSalt()));
        }

        // 更改邮箱逻辑
        if (StringUtils.isNotBlank(reqDTO.getNewEmail())) {
            AssertUtils.isTrue(!isExistEmail(reqDTO.getNewEmail()), ApiExceptionEnum.EMAIL_EXIST);
            updateDTO.setEmail(reqDTO.getNewEmail());
            updateDTO.setEmailVerified(0);
        }

        userDao.updateById(updateDTO);

        // 邮箱验证邮件
        if (StringUtils.isNotBlank(reqDTO.getNewEmail())) {
            String uuid = UUID.randomUUID().toString();
            redisUtils.set(RedisConstants.getEmailVerificationKey(uuid), userDO.getUsername(), userServiceProperties.getVerificationExpire());
            emailUtil.sendResetEmailMail(userDO.getUsername(), reqDTO.getNewEmail(), uuid);
        }
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
            respDTO.setUser(bindingLogin(userDO, ipv4, userAgent));
            // 临时代码，每次登录后把 nickname 改成 realName
            userDao.lambdaUpdate().set(UserDO::getNickname, realName).eq(UserDO::getUserId, userDO.getUserId()).update();
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
        AssertUtils.isTrue(!isExistUsername(reqDTO.getEmail()), ApiExceptionEnum.EMAIL_EXIST);
        // 取 redis 中的 token
        UserThirdPartyLoginRespDTO respDTO = JSON.parseObject((String) redisUtils.get(RedisConstants.getThirdPartyToken(reqDTO.getToken())),
                UserThirdPartyLoginRespDTO.class);
        AssertUtils.notNull(respDTO, ApiExceptionEnum.THIRD_PARTY_NOT_EXIST);
        // 验证用户名和邮箱存在性
        AssertUtils.isTrue(!isExistUsername(reqDTO.getUsername()), ApiExceptionEnum.USER_EXIST);
        // 创建账号方式
        UserDO userDO = UserDO.builder()
                              .username(reqDTO.getUsername())
                              .email(reqDTO.getEmail())
                              .password(reqDTO.getPassword())
                              .emailVerified(1)
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
        // 登录
        return bindingLogin(userDO, ipv4, userAgent);
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
        // 删除 redis 中对应的 token
        redisUtils.del(RedisConstants.getThirdPartyToken(reqDTO.getToken()));
        // 登录
        return bindingLogin(userDO, ipv4, userAgent);
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
     * 第三方认证中登录并写 session 的代码抽取
     */
    private UserSessionDTO bindingLogin(UserDO userDO, String ipv4, String userAgent) {
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