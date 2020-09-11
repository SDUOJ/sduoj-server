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
public class ProblemCacheUtils {
    // TODO: 设计本地 Guava 缓存

    @Autowired
    private RedisUtils redisUtils;

    public String getProblemTitle(int problemId) {
        Object o = redisUtils.hget(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE, String.valueOf(problemId));
        return o == null ? null : (String) o;
    }

    public int getProblemCheckpointNum(int problemId) {
        Object o = redisUtils.hget(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM, String.valueOf(problemId));
        return o == null ? 0 : (Integer) o;
    }
}