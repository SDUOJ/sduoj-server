/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.cache;

import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;


@Component
public class CommonCacheTypeManager extends AbstractCacheTypeManager {


    public CommonCacheTypeManager() {
        cacheTypeList = Lists.newArrayList(
                new CacheType(RedisConstants.ALL_URL_PERMISSION, 60 * 5),
                new CacheType(RedisConstants.URL_TO_ROLES_MAP, 60 * 5),
                new CacheType(RedisConstants.URL_TO_ROLES, 60 * 5),

                new CacheType(RedisConstants.USER_ID_TO_USERNAME, 60 * 5),
                new CacheType(RedisConstants.USERNAME_TO_USERID, 60 * 5),
                new CacheType(RedisConstants.USER_ID_TO_ROLES, 60),

                new CacheType(RedisConstants.PROBLEM_ID_TO_PROBLEM_TITLE, 60),
                new CacheType(RedisConstants.PROBLEM_ID_TO_PROBLEM_CHECKPOINT_NUM, 60),
                new CacheType(RedisConstants.PROBLEM_CODE_TO_PROBLEM_ID, 60 * 5),
                new CacheType(RedisConstants.PROBLEM_ID_TO_PROBLEM_CODE, 60 * 5)

                );
    }

}
