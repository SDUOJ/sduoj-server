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

/**
 * @ClassName UserController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Controller
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/{id}")
    @ApiResponseBody
    public User queryById(@PathVariable("id") Integer id) {
        return this.userService.queryById(id);
    }


    /**
     * 根据用户名和密码查询用户, 内部接口
     * TODO: 实现通用返回类
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/query")
    @ResponseBody
    public User queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password) throws InternalApiException {
        User user = this.userService.queryUser(username, password);
        return user;
    }

    /**
     * 根据用户id查询用户, 内部接口
     * TODO: 实现通用返回类
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/queryById")
    @ResponseBody
    public User queryUser(@RequestParam("id") Integer id) {
        User user = this.userService.queryById(id);
        return user;
    }
}
