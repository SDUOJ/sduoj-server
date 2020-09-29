package cn.edu.sdu.qd.oj.user.dao;

import cn.edu.sdu.qd.oj.user.entity.UserExtensionDO;
import cn.edu.sdu.qd.oj.user.mapper.UserExtensionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class UserExtensionDao extends ServiceImpl<UserExtensionMapper, UserExtensionDO> {
}
