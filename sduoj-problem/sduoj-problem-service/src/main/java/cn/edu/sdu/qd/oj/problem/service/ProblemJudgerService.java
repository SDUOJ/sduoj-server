/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.service;

import cn.edu.sdu.qd.oj.problem.converter.ProblemJudgerConverter;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemJudgerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName ProblemJudgerService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:33
 * @Version V1.0
 **/

@Service
public class ProblemJudgerService {
    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private ProblemJudgerConverter problemJudgerConverter;

    public ProblemJudgerDTO queryById(Long problemId) {
        ProblemDO problemJudgerDO = problemDao.lambdaQuery().select(
            ProblemDO::getProblemId,
            ProblemDO::getIsPublic,
            ProblemDO::getTimeLimit,
            ProblemDO::getMemoryLimit,
            ProblemDO::getCheckpointNum,
            ProblemDO::getCheckpoints
        ).eq(ProblemDO::getProblemId, problemId).one();
        return problemJudgerConverter.to(problemJudgerDO);
    }
}