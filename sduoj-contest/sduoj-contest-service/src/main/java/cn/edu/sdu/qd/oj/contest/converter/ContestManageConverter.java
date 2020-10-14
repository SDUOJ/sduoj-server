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

import cn.edu.sdu.qd.oj.contest.dto.ContestManageDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;

@org.mapstruct.Mapper(
        componentModel = "spring",
        imports = {ContestConvertUtils.class}
)
public interface ContestManageConverter extends BaseContestConverter<ContestDO, ContestManageDTO> {

    @org.mapstruct.Mapping(
            target = "username",
            expression = "java( ContestConvertUtils.userIdToUsername(source.getUserId()) )"
    )
    ContestManageDTO to(ContestDO source);


}