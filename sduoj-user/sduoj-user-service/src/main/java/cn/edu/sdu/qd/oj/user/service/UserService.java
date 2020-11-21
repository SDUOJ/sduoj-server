/*
 * Copyright 2020-2020 the original author or authors.
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
import cn.edu.sdu.qd.oj.user.config.UserServiceProperties;
import cn.edu.sdu.qd.oj.user.converter.UserConverter;
import cn.edu.sdu.qd.oj.user.converter.UserSessionConverter;
import cn.edu.sdu.qd.oj.user.dao.UserDao;
import cn.edu.sdu.qd.oj.user.dao.UserSessionDao;
import cn.edu.sdu.qd.oj.user.dto.UserUpdateReqDTO;
import cn.edu.sdu.qd.oj.user.entity.UserDO;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.entity.UserSessionDO;
import cn.edu.sdu.qd.oj.common.util.CodecUtils;
import cn.edu.sdu.qd.oj.user.utils.EmailUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

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

    public UserDTO verify(Long userId) {
        UserDO userDO = userDao.getById(userId);
        AssertUtils.notNull(userDO, ApiExceptionEnum.USER_NOT_FOUND);
        return userConverter.to(userDO);
    }

    public UserDTO verify(String username, String password) throws ApiException {
        return userConverter.to(verifyAndGetDO(username, password));
    }

    /**
     * @param username
     * @param password
     * @return UserDO notnull
     * @Description 校验用户账号密码并返回 DO
     **/
    public @NotNull UserDO verifyAndGetDO(String username, String password) throws ApiException {
        // 查询
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
        userDO.setNickname(userDO.getUsername());
        // TODO: username 重复时插入失败的异常处理器
        try {
            AssertUtils.isTrue(userDao.save(userDO), ApiExceptionEnum.UNKNOWN_ERROR);
        } catch (DuplicateKeyException e) {
            throw new ApiException(ApiExceptionEnum.USER_EXIST);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }

        sendVerificationEmail(userDTO.getUsername(), userDTO.getEmail());

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
        String username = Optional.ofNullable(redisUtils.get(RedisConstants.getEmailVerificationKey(token))).map(o -> (String) o).orElse(null);
        AssertUtils.notNull(username, ApiExceptionEnum.TOKEN_EXPIRE);
        // 单字段更新，不需要先查后改，不需要乐观锁
        AssertUtils.isTrue(userDao.lambdaUpdate()
                    .eq(UserDO::getUsername, username)
                    .set(UserDO::getEmailVerified, 1)
                    .set(UserDO::getRoles, PermissionEnum.USER.name)
                    .update(), ApiExceptionEnum.UNKNOWN_ERROR);
    }

    public UserDTO queryByUserId(Long userId) {
        UserDO userDO = userDao.getById(userId);
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



    public Long queryUserId(String username) {
        return Optional.ofNullable(userDao.lambdaQuery().eq(UserDO::getUsername, username).select(UserDO::getUserId).one())
                .map(UserDO::getUserId)
                .orElse(null);
    }

    public String queryUsername(Long userId) {
        return Optional.ofNullable(userDao.lambdaQuery().select(UserDO::getUserId, UserDO::getUsername).eq(UserDO::getUserId, userId).one())
                .map(UserDO::getUsername)
                .orElse(null);
    }

    public String queryNickname(Long userId) {
        return Optional.ofNullable(userDao.lambdaQuery().select(UserDO::getUserId, UserDO::getNickname).eq(UserDO::getUserId, userId).one())
                .map(UserDO::getNickname)
                .orElse(null);
    }
}