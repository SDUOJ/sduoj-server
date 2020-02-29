/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import cn.edu.sdu.qd.oj.user.pojo.User;
import cn.edu.sdu.qd.oj.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseResult> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ResponseResult.ok(this.userService.queryById(id)));
    }


    /**
     * 根据用户名和密码查询用户
     * TODO: 实现通用返回类
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        User user = this.userService.queryUser(username, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }
}
