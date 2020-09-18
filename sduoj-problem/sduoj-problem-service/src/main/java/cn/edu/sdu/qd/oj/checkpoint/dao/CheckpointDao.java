package cn.edu.sdu.qd.oj.checkpoint.dao;

import cn.edu.sdu.qd.oj.checkpoint.entity.CheckpointDO;
import cn.edu.sdu.qd.oj.checkpoint.mapper.CheckpointDOMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class CheckpointDao extends ServiceImpl<CheckpointDOMapper, CheckpointDO> {
}
