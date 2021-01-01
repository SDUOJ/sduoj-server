/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.cache;

import cn.edu.sdu.qd.oj.common.cache.AbstractCacheTypeManager;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;


@Component
public class ContestCacheTypeManager extends AbstractCacheTypeManager {

    public static final String RAW_RANK = "RawRank";
    public static final String RANK = "Rank";
    public static final String SUBMISSION_RESULT_LIST = "SubmissionResultList";
    public static final String CONTEST_OVERVIEW = "ContestOverview";

    public ContestCacheTypeManager() {
        cacheTypeList = Lists.newArrayList(
                new CacheType(RAW_RANK, 30),
                new CacheType(RANK, 15),
                new CacheType(SUBMISSION_RESULT_LIST, 10),
                new CacheType(CONTEST_OVERVIEW, 10)
        );
    }

}
