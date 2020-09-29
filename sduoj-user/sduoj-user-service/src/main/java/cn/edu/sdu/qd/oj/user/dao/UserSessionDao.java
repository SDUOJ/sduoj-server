package cn.edu.sdu.qd.oj.user.dao;

import cn.edu.sdu.qd.oj.user.entity.UserSessionDO;
import cn.edu.sdu.qd.oj.user.mapper.UserSessionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class UserSessionDao extends ServiceImpl<UserSessionMapper, UserSessionDO> {
}
