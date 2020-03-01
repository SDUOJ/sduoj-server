/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.api;

import cn.edu.sdu.qd.oj.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @InterfaceName UserApi
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/27 14:56
 * @Version V1.0
 **/

public interface UserApi {
    @GetMapping("/query")
    User queryUser(@RequestParam("username") String username,
                   @RequestParam("password") String password);
    @GetMapping("/queryById")
    User queryUser(@RequestParam("id") Integer id);
}