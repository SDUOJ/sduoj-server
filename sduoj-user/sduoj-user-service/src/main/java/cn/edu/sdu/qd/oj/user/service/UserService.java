/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.service;

import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
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
import cn.edu.sdu.qd.oj.user.utils.CodecUtils;
import cn.edu.sdu.qd.oj.user.utils.EmailUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
        if (userDO == null) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
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
        UserDO userDO = userDao.lambdaQuery().eq(UserDO::getUsername, username).one();
        if (userDO == null) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
        if (!userDO.getPassword().equals(CodecUtils.md5Hex(password, userDO.getSalt()))) {
            throw new ApiException(ApiExceptionEnum.PASSWORD_NOT_MATCHING);
        }
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
        // TODO: username 重复时插入失败的异常处理器
        try {
            if (!userDao.save(userDO)) {
                throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
            }
        } catch (DuplicateKeyException e) {
            throw new ApiException(ApiExceptionEnum.USER_EXIST);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }

        sendVerificationEmail(userDTO.getUsername(), userDTO.getEmail());

        // 更新缓存
        redisUtils.hset(RedisConstants.REDIS_KEY_FOR_USER_ID_TO_USERNAME,
                String.valueOf(userDO.getUserId()), userDO.getUsername());
    }

    public Long queryUserId(String username) {
        UserDO userDO = userDao.lambdaQuery().eq(UserDO::getUsername, username).select(UserDO::getUserId).one();
        return Optional.of(userDO).map(UserDO::getUserId).orElse(null);
    }

    public Map<Long, String> queryIdToUsernameMap() {
        List<UserDO> userDOList = userDao.lambdaQuery().select(UserDO::getUserId, UserDO::getUsername).list();
        return userDOList.stream().collect(Collectors.toMap(UserDO::getUserId, UserDO::getUsername, (k1, k2) -> k1));
    }

    public List<String> queryRolesById(Long userId) {
        UserDO userDO = userDao.lambdaQuery().select(UserDO::getRoles).eq(UserDO::getUserId, userId).one();
        return Optional.ofNullable(userDO).map(userConverter::to).map(UserDTO::getRoles).orElse(null);
    }

    @PostConstruct
    public void initRedisUserHash() {
        List<UserDO> userDOList = userDao.lambdaQuery().select(
                UserDO::getUserId, 
                UserDO::getUsername
        ).list();
        Map<String, Object> map1 = userDOList.stream().collect(Collectors.toMap(userDo -> userDo.getUserId().toString(), UserDO::getUsername, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_USER_ID_TO_USERNAME, map1);
        Map<String, Object> map2 = userDOList.stream().collect(Collectors.toMap(UserDO::getUsername, UserDO::getUserId, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_USERNAME_TO_ID, map2);
    }

    public void emailVerify(String token) {
        String username = Optional.ofNullable(redisUtils.get(RedisConstants.getEmailVerificationKey(token))).map(o -> (String) o).orElse(null);
        if (username == null) {
            throw new ApiException(ApiExceptionEnum.TOKEN_EXPIRE);
        }
        // TODO: 乐观锁处理, mbp自动处理
        if (!userDao.lambdaUpdate().eq(UserDO::getUsername, username).set(UserDO::getEmailVerified, 1).update()) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
    }

    public UserDTO queryByUserId(Long userId) {
        UserDO userDO = userDao.getById(userId);
        if (userDO == null) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
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
        if (userDO == null) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
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
        if (userDO == null) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
        email = userDO.getEmail();
        // 发送验证邮件
        String uuid = UUID.randomUUID().toString();
        redisUtils.set(RedisConstants.getForgetPasswordKey(uuid), userDO.getUsername(), userServiceProperties.getVerificationExpire());
        emailUtil.sendForgetPasswordEmail(userDO.getUsername(), userDO.getEmail(), uuid);
        return email;
    }

    public void resetPassword(String token, String password) {
        String username = Optional.ofNullable(redisUtils.get(RedisConstants.getForgetPasswordKey(token))).map(o -> (String) o).orElse(null);
        if (username == null) {
            throw new ApiException(ApiExceptionEnum.TOKEN_EXPIRE);
        }

        String salt = CodecUtils.generateSalt();
        String saltPassword = CodecUtils.md5Hex(password, salt);

        if (!userDao.lambdaUpdate().eq(UserDO::getUsername, username).set(UserDO::getSalt, salt).set(UserDO::getPassword, saltPassword).update()) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
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
        if (userDO == null) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
        if (!CodecUtils.md5Hex(reqDTO.getPassword(), userDO.getSalt()).equals(userDO.getPassword())) {
            throw new ApiException(ApiExceptionEnum.PASSWORD_NOT_MATCHING);
        }

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
            if (isExistEmail(reqDTO.getNewEmail())) {
                throw new ApiException(ApiExceptionEnum.EMAIL_EXIST);
            }
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
}
