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

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.problem.dao.ProblemExtensionDao;
import cn.edu.sdu.qd.oj.problem.dto.ProblemCaseDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemExtensionDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemExtensionDOField;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProblemExtensionSerivce {

    @Autowired
    private ProblemExtensionDao problemExtensionDao;


    public void updateProblemCase(long problemId, List<ProblemCaseDTO> problemCaseDTOList) {
        ProblemExtensionDO originalProblemExtensionDO = problemExtensionDao.lambdaQuery().select(
                ProblemExtensionDO::getId,
                ProblemExtensionDO::getVersion
        ).eq(ProblemExtensionDO::getProblemId, problemId)
         .eq(ProblemExtensionDO::getExtensionKey, ProblemExtensionDOField.problemCase())
         .one();
        if (originalProblemExtensionDO == null) {
            ProblemExtensionDO problemExtensionDO = ProblemExtensionDO.builder()
                    .problemId(problemId)
                    .extensionKey(ProblemExtensionDOField.problemCase())
                    .extensionValue(JSON.toJSONString(problemCaseDTOList))
                    .build();
            AssertUtils.isTrue(problemExtensionDao.save(problemExtensionDO), ApiExceptionEnum.SERVER_BUSY);
        } else {
            ProblemExtensionDO problemExtensionDO = ProblemExtensionDO.builder()
                    .id(originalProblemExtensionDO.getId())
                    .version(originalProblemExtensionDO.getVersion())
                    .extensionValue(JSON.toJSONString(problemCaseDTOList))
                    .build();
            AssertUtils.isTrue(problemExtensionDao.updateById(problemExtensionDO), ApiExceptionEnum.SERVER_BUSY);
        }
    }

    public List<ProblemCaseDTO> queryProblemCase(long problemId) {
        ProblemExtensionDO problemExtensionDO = problemExtensionDao.lambdaQuery()
                .eq(ProblemExtensionDO::getProblemId, problemId)
                .eq(ProblemExtensionDO::getExtensionKey, ProblemExtensionDOField.problemCase())
                .one();
        String value = Optional.ofNullable(problemExtensionDO).map(ProblemExtensionDO::getExtensionValue).orElse(null);
        if (StringUtils.isBlank(value)) {
            return Lists.newArrayList();
        }
        try {
            return JSON.parseObject(value, new TypeReference<List<ProblemCaseDTO>>() {});
        } catch (Throwable t) {
            log.warn("{}", t.getMessage());
            return Lists.newArrayList();
        }
    }
}
