/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.user.service;

import cn.edu.sdu.qd.oj.common.enums.ExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.OJException;
import cn.edu.sdu.qd.oj.user.mapper.UserMapper;
import cn.edu.sdu.qd.oj.user.pojo.User;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User queryById(Integer id) {
        User user = this.userMapper.selectByPrimaryKey(id);
        if (user == null) {
            throw new OJException(ExceptionEnum.USER_NOT_FOUND);
        }
        return user;
    }
}
