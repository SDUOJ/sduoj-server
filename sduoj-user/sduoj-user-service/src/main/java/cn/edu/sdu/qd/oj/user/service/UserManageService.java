/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.service;

import cn.edu.sdu.qd.oj.auth.enums.PermissionEnum;
import cn.edu.sdu.qd.oj.common.entity.PageResult;
import cn.edu.sdu.qd.oj.user.converter.UserConverter;
import cn.edu.sdu.qd.oj.user.converter.UserManageUpdateConverter;
import cn.edu.sdu.qd.oj.user.dao.UserDao;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.dto.UserListReqDTO;
import cn.edu.sdu.qd.oj.user.dto.UserManageUpdateReqDTO;
import cn.edu.sdu.qd.oj.user.entity.UserDO;
import cn.edu.sdu.qd.oj.common.util.CodecUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class UserManageService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserManageUpdateConverter userManageUpdateConverter;

    public PageResult<UserDTO> list(@Valid @NotNull UserListReqDTO reqDTO) {
        LambdaQueryChainWrapper<UserDO> query = userDao.lambdaQuery();

        Optional.of(reqDTO).map(UserListReqDTO::getUsername).ifPresent(username -> {
            query.likeRight(UserDO::getUsername, username);
        });

        Optional.of(reqDTO).map(UserListReqDTO::getStudentId).ifPresent(studentId -> {
            query.likeRight(UserDO::getStudentId, studentId);
        });

        Optional.of(reqDTO).map(UserListReqDTO::getPhone).ifPresent(phone -> {
            query.likeRight(UserDO::getPhone, phone);
        });

        Optional.of(reqDTO).map(UserListReqDTO::getEmail).ifPresent(email -> {
            query.likeRight(UserDO::getEmail, email);
        });

        Page<UserDO> pageResult = query.page(new Page<>(reqDTO.getPageNow(), reqDTO.getPageSize()));

        return new PageResult<>(pageResult.getPages(), userConverter.to(pageResult.getRecords()));
    }

    public void update(UserManageUpdateReqDTO userDTO) {
        UserDO userDO = userManageUpdateConverter.from(userDTO);

        Optional.of(userDO).map(UserDO::getPassword).ifPresent(password -> {
            userDO.setSalt(CodecUtils.generateSalt());
            userDO.setPassword(CodecUtils.md5Hex(password, userDO.getSalt()));
        });

        userDao.lambdaUpdate().eq(UserDO::getUsername, userDO.getUsername()).update(userDO);
    }

    @Transactional
    public void addUsers(List<UserDTO> userDTOList) {
        List<UserDO> userDOList = userConverter.from(userDTOList);

        userDOList.forEach(userDO -> {
            userDO.setSalt(CodecUtils.generateSalt());
            userDO.setPassword(CodecUtils.md5Hex(userDO.getPassword(), userDO.getSalt()));
            userDO.setEmailVerified(1);
            userDO.setRoles(PermissionEnum.USER.name);
        });

        userDao.saveBatch(userDOList);
    }
}