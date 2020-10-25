/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.problem.dto.ProblemCaseDTO;
import cn.edu.sdu.qd.oj.problem.dto.ProblemDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;
import org.mapstruct.factory.Mappers;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProblemConverter extends BaseProblemConverter<ProblemDO, ProblemDTO> {

    ProblemDescriptionConverter problemDescriptionConverter = Mappers.getMapper(ProblemDescriptionConverter.class);

    ProblemDescriptionListConverter problemDescriptionListConverter = Mappers.getMapper(ProblemDescriptionListConverter.class);

    default ProblemDTO to(ProblemDO problemDO,
                          ProblemDescriptionDO problemDescriptionDO,
                          List<ProblemDescriptionDO> problemDescriptionDOList,
                          List<ProblemCaseDTO> problemCaseDTOList) {
        ProblemDTO problemDTO = to(problemDO);
        problemDTO.setProblemDescriptionDTO(problemDescriptionConverter.to(problemDescriptionDO));
        problemDTO.setProblemDescriptionListDTOList(problemDescriptionListConverter.to(problemDescriptionDOList));
        problemDTO.setProblemCaseDTOList(problemCaseDTOList);
        return problemDTO;
    }
}