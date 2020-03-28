/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.user.pojo.User;
import cn.edu.sdu.qd.oj.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiResponseBody
    public Void register(@Valid @RequestBody User user) {
        this.userService.register(user);
        return null;
    }

    /**
     * 根据用户名和密码查询用户, 内部接口
     * TODO: 实现通用返回类
     * @param username
     * @param password
     * @return
     */
    @PostMapping("internal/verify")
    @ResponseBody
    public User verify(
            @RequestParam("username") String username,
            @RequestParam("password") String password) throws InternalApiException {
        User user = this.userService.query(username, password);
        return user;
    }

    /**
     * 根据用户id查询用户, 内部接口
     * TODO: 实现通用返回类
     * @param username
     * @param password
     * @return
     */
    @PostMapping("internal/query")
    @ResponseBody
    public User query(@RequestParam("userId") Integer userId) {
        User user = this.userService.query(userId);
        return user;
    }
}
