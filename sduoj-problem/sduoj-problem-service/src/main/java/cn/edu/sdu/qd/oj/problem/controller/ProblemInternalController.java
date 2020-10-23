/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.controller;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.problem.api.ProblemApi;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemJudgerDTO;
import cn.edu.sdu.qd.oj.problem.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ProblemInternalController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/9 15:46
 * @Version V1.0
 **/

@RestController
public class ProblemInternalController implements ProblemApi {

    @Autowired
    private ProblemService problemService;

    @Override
    public Map<Long, String> queryIdToTitleMap() throws InternalApiException {
        return problemService.queryIdToTitleMap();
    }

    @Override
    public boolean validateProblemCodeList(List<String> problemCodeList) throws InternalApiException {
        return problemService.validateProblemCodeList(problemCodeList);
    }

    @Override
    public ProblemDTO queryAndValidate(String problemCode, long problemDescriptionId, long userId) {
        return problemService.queryWithDescriptionId(problemCode, problemDescriptionId, userId);
    }

    @Override
    public List<Long> queryPrivateProblemIdList(Long userId) {
        return problemService.queryPrivateProblemIdList(userId);
    }

    @Override
    public ProblemJudgerDTO queryProblemJudgeDTO(Long problemId) {
        // TODO
        return null;
    }

}