package cn.edu.sdu.qd.oj.auth.dao;

import cn.edu.sdu.qd.oj.auth.entity.PermissionDO;
import cn.edu.sdu.qd.oj.auth.mapper.PermissionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionDao extends ServiceImpl<PermissionMapper, PermissionDO> {
}
