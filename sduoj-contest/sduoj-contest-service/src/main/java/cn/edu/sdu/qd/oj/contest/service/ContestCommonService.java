/*
 * Copyright 2020-2021 the original author or authors.
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
import cn.edu.sdu.qd.oj.contest.dto.ContestDTO;
import cn.edu.sdu.qd.oj.contest.dto.ContestManageDTO;
import cn.edu.sdu.qd.oj.contest.entity.ContestDO;
import org.springframework.stereotype.Service;

@Service
public class ContestCommonService {

    public boolean isContestManager(ContestDTO contestDTO, UserSessionDTO userSessionDTO) {
        return isContestManager(contestDTO.getUserId(), userSessionDTO);
    }

    public boolean isContestManager(ContestManageDTO contestManageDTO, UserSessionDTO userSessionDTO) {
        return isContestManager(contestManageDTO.getUserId(), userSessionDTO);
    }

    public boolean isContestManager(ContestDO contestDO, UserSessionDTO userSessionDTO) {
        return isContestManager(contestDO.getUserId(), userSessionDTO);
    }

    /**
    * 比赛管理员：超级管理员、比赛创建者、权限用户组成员
    **/
    public boolean isContestManager(Long ownerUserId, UserSessionDTO userSessionDTO) {
        return PermissionEnum.SUPERADMIN.in(userSessionDTO) ||
                userSessionDTO.userIdEquals(ownerUserId);
    }
}
