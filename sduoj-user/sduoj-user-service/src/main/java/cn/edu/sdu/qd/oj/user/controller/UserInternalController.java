/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.user.api.UserApi;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.service.UserExtensionService;
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

    @Autowired
    private UserExtensionService userExtensionService;

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


    @Override
    public void addUserACProblem(long userId, long contestId, long problemId) {
        // TODO: 外置的自动 retry 机制
        for (int i = 0; i < 5; i++) {
            try {
                userExtensionService.addUserACProblem(userId, contestId, problemId);
            } catch (ApiException e) {
                if (ApiExceptionEnum.SERVER_BUSY.code == e.code) {
                    continue;
                }
            }
            break;
        }
    }

    @Override
    public void addUserParticipateContest(long userId, long contestId) {
        // TODO: 外置的自动 retry 机制
        for (int i = 0; i < 5; i++) {
            try {
                userExtensionService.addUserParticipateContest(userId, contestId);
            } catch (ApiException e) {
                if (ApiExceptionEnum.SERVER_BUSY.code == e.code) {
                    continue;
                }
            }
            break;
        }
    }

    @Override
    public List<String> queryACProblem(long userId, long contestId) {
        return userExtensionService.queryACProblem(userId, contestId);
    }

}