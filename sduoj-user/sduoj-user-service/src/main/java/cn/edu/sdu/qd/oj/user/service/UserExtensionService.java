/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.service;

import cn.edu.sdu.qd.oj.common.converter.BaseConvertUtils;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.user.dao.UserExtensionDao;
import cn.edu.sdu.qd.oj.user.entity.UserExtensionDO;
import cn.edu.sdu.qd.oj.user.entity.UserExtensionDOField;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserExtensionService {

    @Autowired
    private UserExtensionDao userExtensionDao;

    public void addUserParticipateContest(long userId, long contestId) {
        setIncrementalUpdate(userId, UserExtensionDOField.participateContest(), String.valueOf(contestId));
    }

    private void setIncrementalUpdate(long userId, String key, String incVal) {
        UserExtensionDO userExtensionDO = getUserExtensionDO(userId, key);
        Set<String> problemIdList = Optional.ofNullable(userExtensionDO.getExtensionValue()).map(BaseConvertUtils::stringToSet).orElse(Sets.newHashSet());
        if (!problemIdList.add(incVal)) {
            return;
        }
        userExtensionDO.setExtensionValue(BaseConvertUtils.setToString(problemIdList));
        AssertUtils.isTrue(userExtensionDao.saveOrUpdate(userExtensionDO), ApiExceptionEnum.SERVER_BUSY);
    }

    private UserExtensionDO getUserExtensionDO(long userId, String key) {
        UserExtensionDO userExtensionDO = userExtensionDao.lambdaQuery()
                .eq(UserExtensionDO::getUserId, userId)
                .eq(UserExtensionDO::getExtensionKey, key)
                .one();
        if (userExtensionDO == null) {
            userExtensionDO = UserExtensionDO.builder().userId(userId).extensionKey(key).build();
        }
        return userExtensionDO;
    }

    public List<Long> queryParticipateContest(long userId) {
        UserExtensionDO userExtensionDO = getUserExtensionDO(userId, UserExtensionDOField.participateContest());
        List<String> problemIdList = Optional.ofNullable(userExtensionDO.getExtensionValue()).map(BaseConvertUtils::stringToList).orElse(Lists.newArrayList());
        return problemIdList.stream()
                .filter(StringUtils::isNumeric)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}