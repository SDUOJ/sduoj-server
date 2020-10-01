/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserCacheService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/8 11:33
 * @Version V1.0
 **/

@Service
@Slf4j
public class UserCacheUtils {

    @Autowired
    private RedisUtils redisUtils;

    public String getUsername(long userId) {
        Object o = redisUtils.hget(RedisConstants.REDIS_KEY_FOR_USER_ID_TO_USERNAME, String.valueOf(userId));
        // TODO: 设计本地 Guava 缓存
        return o == null ? null : (String) o;
    }
}