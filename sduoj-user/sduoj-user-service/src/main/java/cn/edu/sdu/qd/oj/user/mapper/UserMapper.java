/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/


package cn.edu.sdu.qd.oj.user.mapper;

import cn.edu.sdu.qd.oj.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName UserMapper
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/
public interface UserMapper extends Mapper<User> {

    @Select("SELECT u_id FROM oj_users WHERE u_username=#{username}")
    public Integer queryUserId(@Param("username") String username);
}
