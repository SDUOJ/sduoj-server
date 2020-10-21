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

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.contest.converter.ContestConverter;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.contest.converter.ContestCreateReqConverter;
import cn.edu.sdu.qd.oj.contest.converter.ContestManageConverter;
import cn.edu.sdu.qd.oj.contest.dao.ContestDao;
import cn.edu.sdu.qd.oj.contest.dto.ContestCreateReqDTO;
import cn.edu.sdu.qd.oj.contest.dto.ContestDTO;
import cn.edu.sdu.qd.oj.contest.dto.ContestManageDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContestManageService {

    @Autowired
    private ContestDao contestDao;

    @Autowired
    private ContestCreateReqConverter contestCreateReqConverter;

    @Autowired
    private ContestConverter contestConverter;

    @Autowired
    private ContestManageConverter contestManageConverter;

    public Long create(ContestCreateReqDTO reqDTO) {
        ContestDO contestDO = contestCreateReqConverter.from(reqDTO);
        AssertUtils.isTrue(contestDao.save(contestDO), ApiExceptionEnum.UNKNOWN_ERROR);
        return contestDO.getContestId();
    }


    public ContestManageDTO query(long contestId) {
        ContestDO contestDO = contestDao.getById(contestId);
        if (contestDO == null) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_FOUND);
        }
        return contestManageConverter.to(contestDO);
    }

    public void update(ContestDTO reqDTO, UserSessionDTO userSessionDTO) {
        ContestDO contestDO = contestDao.lambdaQuery().select(
                ContestDO::getContestId,
                ContestDO::getUserId,
                ContestDO::getVersion
        ).eq(ContestDO::getContestId, reqDTO.getContestId()).one();
        if (contestDO == null) {
            throw new ApiException(ApiExceptionEnum.CONTEST_NOT_FOUND);
        }
        // 超级管理员一定可以更新比赛详情，除此之外只有出题者能
        if (PermissionEnum.SUPERADMIN.notIn(userSessionDTO) && userSessionDTO.userIdNotEquals(contestDO.getUserId())) {
            throw new ApiException(ApiExceptionEnum.USER_NOT_MATCHING);
        }

        reqDTO.setParticipantNum(Optional.ofNullable(reqDTO.getParticipants()).map(List::size).orElse(0));

        ContestDO contestUpdateDO = contestConverter.from(reqDTO);
        contestUpdateDO.setVersion(contestDO.getVersion());

        if (!contestDao.updateById(contestUpdateDO)) {
            throw new ApiException(ApiExceptionEnum.SERVER_BUSY);
        }
    }
}