/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.service;

import cn.edu.sdu.qd.oj.common.config.RedisConstants;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.common.utils.RedisUtils;
import cn.edu.sdu.qd.oj.user.mapper.UserMapper;
import cn.edu.sdu.qd.oj.user.pojo.User;
import cn.edu.sdu.qd.oj.user.utils.CodecUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private UserMapper userMapper;

    @Autowired
    private RedisUtils redisUtils;

    public User query(Integer userId) {
        User user = this.userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
        return user;
    }

    public User query(String username, String password) throws InternalApiException {
        // 查询
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        // 校验用户名
        if (user == null) {
            throw new InternalApiException(ApiExceptionEnum.USER_NOT_FOUND);
        }
        // 临时用
        if (!user.getPassword().equals(CodecUtils.md5Hex(password, "slat_string"))) {
            throw new InternalApiException(ApiExceptionEnum.PASSWORD_NOT_MATCHING);
        }
        // TODO：校验加盐密码，选择加密方式和盐，盐放到配置文件中
//        if (!user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
//            throw new InternalApiException(ExceptionEnum.PASSWORD_NOT_MATCHING);
//        }
        // 用户名密码都正确
        return user;
    }

    public void register(User user) {
        user.setUserId(null);
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), "slat_string"));
        // TODO: username 重复时插入失败的异常处理器
        if(this.userMapper.insertSelective(user) != 1) {
            throw new ApiException(ApiExceptionEnum.UNKNOWN_ERROR);
        }
        // 更新缓存
        redisUtils.hset(RedisConstants.REDIS_KEY_FOR_USER_ID_TO_USERNAME,
                String.valueOf(user.getUserId()),
                user.getUsername());
    }

    public Integer queryUserId(String username) {
        return userMapper.queryUserId(username);
    }

    public Map<Integer, String> queryIdToNameMap() {
        List<Map> list = userMapper.queryIdToNameMap();
        Map<Integer, String> ret = new HashMap<>(list.size());
        // TODO: 魔法值解决
        list.stream().forEach(map -> ret.put((Integer)map.get("u_id"), (String)map.get("u_username")));
        return ret;
    }

    @PostConstruct
    public void initRedisUserHash() {
        List<Map> list = userMapper.queryIdToNameMap();
        Map<String, Object> ret = new HashMap<>(list.size());
        // TODO: 魔法值解决
        list.stream().forEach(map -> ret.put(String.valueOf(map.get("u_id")), map.get("u_username")));
        redisUtils.hmset(RedisConstants.REDIS_KEY_FOR_USER_ID_TO_USERNAME, ret);
    }
}
