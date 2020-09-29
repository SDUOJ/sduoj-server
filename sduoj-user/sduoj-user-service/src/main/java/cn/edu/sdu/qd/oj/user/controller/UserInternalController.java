/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.user.api.UserApi;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName UserInternalController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/4 10:58
 * @Version V1.0
 **/

@RestController
public class UserInternalController implements UserApi {

    @Autowired
    private UserService userService;

    @Override
    public UserDTO verify(Map<String, String> map) throws InternalApiException {
        String username = map.get("username");
        String password = map.get("password");
        return this.userService.verify(username, password);
    }

    @Override
    public UserDTO query(Long userId) throws InternalApiException {
        return this.userService.verify(userId);
    }

    @Override
    public Long queryUserId(String username) throws InternalApiException {
        return this.userService.queryUserId(username);
    }

    @Override
    public Map<Long, String> queryIdToNameMap() throws InternalApiException {
        return userService.queryIdToUsernameMap();
    }

    @Override
    public List<String> queryRolesById(Long userId) {
        return userService.queryRolesById(userId);
    }

}