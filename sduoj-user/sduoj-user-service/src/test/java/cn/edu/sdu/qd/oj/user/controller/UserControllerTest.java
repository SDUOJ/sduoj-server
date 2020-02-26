/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.controller;

import cn.edu.sdu.qd.oj.UserApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName UserControllerTest
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/


@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    public void queryById() {
        System.out.println(userController.queryById(1000));
//        System.out.println(userController.queryById(1));
    }
}
