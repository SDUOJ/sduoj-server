/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateListDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemCheckerConfigDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemCheckpointDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemFunctionTemplateDTO;

import java.util.List;

@org.mapstruct.MapperConfig
public interface BaseProblemConverter<S, T> extends BaseConverter<S, T> {

    default List<ProblemCheckpointDTO> checkpointsTo(byte[] bytes) {
        return ProblemConverterUtils.checkpointsTo(bytes);
    }

    default byte[] checkpointsFrom(List<ProblemCheckpointDTO> checkpointList) {
        return ProblemConverterUtils.checkpointsFrom(checkpointList);
    }

    default List<JudgeTemplateListDTO> judgeTemplatesTo(String judgeTemplates) {
        return ProblemConverterUtils.judgeTemplatesTo(judgeTemplates);
    }

    default String judgeTemplatesFrom(List<JudgeTemplateListDTO> judgeTemplates) {
        return ProblemConverterUtils.judgeTemplatesFrom(judgeTemplates);
    }

    default List<Long> judgeTemplateIdsTo(String judgeTemplates) {
        return ProblemConverterUtils.stringToLongList(judgeTemplates);
    }

    default String judgeTemplateIdsFrom(List<Long> judgeTemplates) {
        return ProblemConverterUtils.longListToString(judgeTemplates);
    }

    default List<Long> checkpointCasesTo(byte[] checkpointCases) {
        return ProblemConverterUtils.bytesToLongList(checkpointCases);
    }

    default byte[] checkpointCasesFrom(List<Long> checkpointCases) {
        return ProblemConverterUtils.longListToBytes(checkpointCases);
    }

    default ProblemCheckerConfigDTO checkerConfigTo(String checkConfig) {
        return ProblemConverterUtils.checkerConfigTo(checkConfig);
    }

    default String checkerConfigFrom(ProblemCheckerConfigDTO checkConfigDTO) {
        return ProblemConverterUtils.checkerConfigFrom(checkConfigDTO);
    }

    default List<ProblemFunctionTemplateDTO> functionTemplatesTo(String functionTemplates) {
        return ProblemConverterUtils.functionTemplatesTo(functionTemplates);
    }

    default String functionTemplatesFrom(List<ProblemFunctionTemplateDTO> functionTemplates) {
        return ProblemConverterUtils.functionTemplatesFrom(functionTemplates);
    }
}