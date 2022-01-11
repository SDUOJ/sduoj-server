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

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.user.converter.UserBatchAddConverter;
import cn.edu.sdu.qd.oj.user.converter.UserConverter;
import cn.edu.sdu.qd.oj.user.converter.UserManageUpdateConverter;
import cn.edu.sdu.qd.oj.user.dao.UserDao;
import cn.edu.sdu.qd.oj.user.dto.UserBatchAddDTO;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.dto.UserListReqDTO;
import cn.edu.sdu.qd.oj.user.dto.UserManageUpdateReqDTO;
import cn.edu.sdu.qd.oj.user.entity.UserDO;
import cn.edu.sdu.qd.oj.common.util.CodecUtils;
import cn.edu.sdu.qd.oj.user.entity.UserDOField;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserManageService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserManageUpdateConverter userManageUpdateConverter;

    @Autowired
    private UserBatchAddConverter userBatchAddConverter;

    public PageResult<UserDTO> list(@Valid @NotNull UserListReqDTO reqDTO) {
        LambdaQueryChainWrapper<UserDO> query = userDao.lambdaQuery();

        Optional.of(reqDTO).map(UserListReqDTO::getUsername).ifPresent(username -> {
            query.like(UserDO::getUsername, username);
        });

        Optional.of(reqDTO).map(UserListReqDTO::getStudentId).ifPresent(studentId -> {
            query.like(UserDO::getStudentId, studentId);
        });

        Optional.of(reqDTO).map(UserListReqDTO::getPhone).ifPresent(phone -> {
            query.like(UserDO::getPhone, phone);
        });

        Optional.of(reqDTO).map(UserListReqDTO::getEmail).ifPresent(email -> {
            query.like(UserDO::getEmail, email);
        });

        Optional.of(reqDTO).map(UserListReqDTO::getSduId).ifPresent(sduId -> {
            query.like(UserDO::getSduId, sduId);
        });

        // searchKey
        Optional.of(reqDTO).map(UserListReqDTO::getSearchKey).filter(StringUtils::isNotBlank).ifPresent(searchKey -> {
            query.or(o1 -> o1.like(UserDO::getUsername, searchKey))
                 .or(o1 -> o1.like(UserDO::getNickname, searchKey))
                 .or(o1 -> o1.like(UserDO::getStudentId, searchKey))
                 .or(o1 -> o1.like(UserDO::getPhone, searchKey))
                 .or(o1 -> o1.like(UserDO::getEmail, searchKey))
                 .or(o1 -> o1.like(UserDO::getSduId, searchKey));
        });
        // 管理员尽量排前面
        query.last("ORDER BY LENGTH(" + UserDOField.ROLES + ") DESC, " + UserDOField.ID);

        Page<UserDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));
        return new PageResult<>(pageResult.getPages(), userConverter.to(pageResult.getRecords()));
    }

    public void update(UserManageUpdateReqDTO userDTO, UserSessionDTO userSessionDTO) {
        UserDO userDO = userManageUpdateConverter.from(userDTO);
        Optional.of(userDO).map(UserDO::getPassword).ifPresent(password -> {
            userDO.setSalt(CodecUtils.generateSalt());
            userDO.setPassword(CodecUtils.md5Hex(password, userDO.getSalt()));
        });

        LambdaUpdateChainWrapper<UserDO> updater = userDao.lambdaUpdate();
        // email=null 不修改, email="" 即清空, 这个语义需要特别注意
        if (userDTO.getEmail() != null && StringUtils.isBlank(userDTO.getEmail())) {
            updater.set(UserDO::getEmail, null);
            userDO.setEmail(null);
        }
        updater.eq(UserDO::getUsername, userDO.getUsername()).update(userDO);

        // 打一下操作日志
        userDTO.setPassword(null);
        log.info("manager: {} update user info: {}", userSessionDTO.getUsername(), userDTO);
    }

    @Transactional
    public void addUsers(List<UserBatchAddDTO> userDTOList) {
        List<UserDO> userDOList = userBatchAddConverter.from(userDTOList);

        userDOList.forEach(userDO -> {
            userDO.setSalt(CodecUtils.generateSalt());
            userDO.setPassword(CodecUtils.md5Hex(userDO.getPassword(), userDO.getSalt()));
            userDO.setRoles(PermissionEnum.USER.name);
            if (userDO.getNickname() == null) {
                userDO.setNickname(userDO.getUsername());
            }
        });

        userDao.saveBatch(userDOList);
    }

    public void delete(List<String> usernameList) {
        userDao.lambdaUpdate().in(UserDO::getUsername, usernameList).remove();
    }
}