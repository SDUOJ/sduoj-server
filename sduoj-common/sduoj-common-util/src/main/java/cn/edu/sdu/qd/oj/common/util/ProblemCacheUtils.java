/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

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

    public String getProblemTitle(long problemId) {
        Object o = redisUtils.hget(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_TITLE, String.valueOf(problemId));
        return o == null ? null : (String) o;
    }

    public int getProblemCheckpointNum(long problemId) {
        Object o = redisUtils.hget(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_CHECKPOINTNUM, String.valueOf(problemId));
        return o == null ? 0 : (Integer) o;
    }

    public long getProblemId(String problemCode) {
        if (problemCode == null) {
            throw new RuntimeException("param error");
        }
        Object o = redisUtils.hget(RedisConstants.REDIS_KEY_FOR_PROBLEM_CODE_TO_PROBLEM_ID, problemCode);
        if (o == null) {
            throw new RuntimeException("cache error, no data");
        }
        return Long.parseLong(String.valueOf(o));
    }

    public String getProblemCode(long problemId) {
        Object o = redisUtils.hget(RedisConstants.REDIS_KEY_FOR_PROBLEM_ID_TO_PROBLEM_CODE, String.valueOf(problemId));
        return o == null ? null : (String) o;
    }
}