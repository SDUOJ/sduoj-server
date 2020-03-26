/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.auth.service;

import cn.edu.sdu.qd.oj.auth.client.UserClient;
import cn.edu.sdu.qd.oj.auth.config.JwtProperties;
import cn.edu.sdu.qd.oj.auth.entity.UserInfo;
import cn.edu.sdu.qd.oj.auth.utils.JwtUtils;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import cn.edu.sdu.qd.oj.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName AuthService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/27 14:18
 * @Version V1.0
 **/
@Service
@Slf4j
public class AuthService {
    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties prop;

    public User authentication(String username, String password) {
        try {
            User user = this.userClient.queryUser(username, password);
            return user;
        } catch (InternalApiException e) {
            log.error(e.toString());
//            throw new ApiException(e.code, e.message);
        }
        return null;
    }

    public User queryUserById(Integer userId) {
        try {
            // 调用微服务，执行查询
            return this.userClient.queryUser(userId);
        } catch (Exception ignore) {
            // TODO: 异常处理
        }
        return null;
    }
}