/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/


package cn.edu.sdu.qd.oj.user.mapper;

import cn.edu.sdu.qd.oj.user.entity.UserDO;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @ClassName UserMapper
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/26 11:29
 * @Version V1.0
 **/
public interface UserMapper extends Mapper<UserDO> {

    @Select("SELECT u_id FROM oj_users WHERE u_username=#{username}")
    public Integer queryUserId(@Param("username") String username);

    @Select("SELECT u_id,u_username FROM oj_users")
    public List<Map> queryIdToNameMap();
}
