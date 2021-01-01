/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.converter;

import cn.edu.sdu.qd.oj.contest.dto.ContestCreateReqDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;

import java.util.List;
import java.util.Optional;

@org.mapstruct.Mapper(
        componentModel = "spring",
        imports = {Optional.class, List.class}
)
public interface ContestCreateReqConverter extends BaseContestConverter<ContestDO, ContestCreateReqDTO> {

    @org.mapstruct.Mapping(
            target = "participantNum",
            expression = "java( Optional.ofNullable(source.getParticipants()).map(List::size).orElse(0) )"
    )
    ContestDO from(ContestCreateReqDTO source);


}