/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.service;

import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.util.RedisUtils;
import cn.edu.sdu.qd.oj.user.converter.UserConverter;
import cn.edu.sdu.qd.oj.user.dao.UserDao;
import cn.edu.sdu.qd.oj.user.entity.UserDO;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.utils.CodecUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserConverter userConverter;

    public UserDTO verify(Long userId) {
        UserDO userDO = userDao.getById(userId);
        if (userDO == null) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
        return userConverter.to(userDO);
    }

    public UserDTO verify(String username, String password) throws InternalApiException {
        // 查询
        UserDO userDO = userDao.lambdaQuery().eq(UserDO::getUsername, username).one();
        // 校验用户名
        if (userDO == null) {
            throw new InternalApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
        // 临时用
        if (!userDO.getPassword().equals(CodecUtils.md5Hex(password, "slat_string"))) {
            throw new InternalApiException(ApiExceptionEnum.PASSWORD_NOT_MATCHING);
        }
        // TODO：校验加盐密码，选择加密方式和盐，盐放到配置文件中
//        if (!user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
//            throw new InternalApiException(ExceptionEnum.PASSWORD_NOT_MATCHING);
//        }
        // 用户名密码都正确
        return userConverter.to(userDO);
    }

    public void register(UserDTO userDTO) {
        UserDO userDO = userConverter.from(userDTO);
        userDO.setPassword(CodecUtils.md5Hex(userDO.getPassword(), "slat_string"));
        // TODO: username 重复时插入失败的异常处理器
        if(!userDao.save(userDO)) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        // 更新缓存
        redisUtils.hset(RedisConstants.REDIS_KEY_FOR_USER_ID_TO_USERNAME,
                String.valueOf(userDO.getUserId()),
                userDO.getUsername());
    }

    public Integer queryUserId(String username) {
        UserDO userDO = userDao.lambdaQuery().eq(UserDO::getUsername, username).select(UserDO::getUserId).one();
        return userDO.getUserId();
    }

    public Map<Integer, String> queryIdToUsernameMap() {
        List<UserDO> userDOList = userDao.lambdaQuery().select(UserDO::getUserId, UserDO::getUsername).list();
        return userDOList.stream().collect(Collectors.toMap(UserDO::getUserId, UserDO::getUsername, (k1, k2) -> k1));
    }

    public List<String> queryRolesById(Long userId) {
        UserDO userDO = userDao.lambdaQuery().select(UserDO::getRoles).eq(UserDO::getUserId, userId).one();
        return Optional.ofNullable(userDO).map(userConverter::to).map(UserDTO::getRoles).orElse(null);
    }

    @PostConstruct
    public void initRedisUserHash() {
        List<UserDO> userDOList = userDao.lambdaQuery().select(UserDO::getUserId, UserDO::getUsername).list();
        Map<String, Object> map = userDOList.stream().collect(Collectors.toMap(userDo -> userDo.getUserId().toString(), UserDO::getUsername, (k1, k2) -> k1));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_USER_ID_TO_USERNAME, map);
    }
}
