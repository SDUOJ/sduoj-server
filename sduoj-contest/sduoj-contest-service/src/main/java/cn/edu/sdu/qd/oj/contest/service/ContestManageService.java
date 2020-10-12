/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.service;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.contest.converter.ContestCreateReqConverter;
import cn.edu.sdu.qd.oj.contest.dao.ContestDao;
import cn.edu.sdu.qd.oj.contest.dto.ContestCreateReqDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContestManageService {

    @Autowired
    private ContestDao contestDao;

    @Autowired
    private ContestCreateReqConverter contestCreateReqConverter;

    public Long create(ContestCreateReqDTO reqDTO) {
        ContestDO contestDO = contestCreateReqConverter.from(reqDTO);
        AssertUtils.isTrue(contestDao.save(contestDO), ApiExceptionEnum.UNKNOWN_ERROR);
        return contestDO.getContestId();
    }


}