/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.contest.dto.ContestProblemListDTO;
import cn.edu.sdu.qd.oj.contest.dto.ContestProblemManageListDTO;
import java.util.List;


@org.mapstruct.MapperConfig
public interface BaseContestConverter<S, T> extends BaseConverter<S, T> {

    default List<ContestProblemListDTO> problemsTo(String problems) {
        return ContestConvertUtils.problemsTo(problems);
    }

    default String problemsFrom(List<ContestProblemListDTO> contestProblemListDTOList) {
        return ContestConvertUtils.problemsFrom(contestProblemListDTOList);
    }

    default List<ContestProblemManageListDTO> problemManagesTo(String problems) {
        return ContestConvertUtils.problemManagesTo(problems);
    }

    default String problemManagesFrom(List<ContestProblemManageListDTO> contestProblemListDTOList) {
        return ContestConvertUtils.problemManagesFrom(contestProblemListDTOList);
    }

    default List<String> participantsTo(byte[] participants) {
        return ContestConvertUtils.participantsTo(participants);
    }

    default byte[] participantsFrom(List<String> participants) {
        return ContestConvertUtils.participantsFrom(participants);
    }
}