/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.user.dto.UserDTO;
import cn.edu.sdu.qd.oj.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName UserInternalController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/4 10:58
 * @Version V1.0
 **/

@Controller
@RequestMapping("/internal/user")
public class UserInternalController {
    @Autowired
    private UserService userService;

    /**
     * 根据用户名和密码查询用户, 内部接口
     * @param username
     * @param password
     * @return user
     */
    @PostMapping("/verify")
    @ResponseBody
    public UserDTO verify(
            @RequestParam("username") String username,
            @RequestParam("password") String password) throws InternalApiException {
        UserDTO userDTO = this.userService.query(username, password);
        return userDTO;
    }

    /**
     * 根据用户id查询用户, 内部接口
     * @param userId
     * @return user
     */
    @PostMapping("/query")
    @ResponseBody
    public UserDTO query(@RequestParam("userId") Integer userId) {
        UserDTO userDTO = this.userService.query(userId);
        return userDTO;
    }

    /**
     * 根据用户名查询用户id, 内部接口
     * @param username
     * @return userId
     */
    @PostMapping("/queryuserid")
    @ResponseBody
    public Integer queryUserId(@RequestParam("username") String username) {
        return this.userService.queryUserId(username);
    }


    @GetMapping("/queryid2name")
    @ResponseBody
    Map<Integer, String> queryAll() {
        return userService.queryIdToUsernameMap();
    }

}