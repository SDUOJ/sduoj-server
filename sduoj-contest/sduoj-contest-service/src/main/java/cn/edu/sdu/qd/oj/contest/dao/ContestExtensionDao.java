package cn.edu.sdu.qd.oj.contest.dao;

import cn.edu.sdu.qd.oj.contest.entity.ContestDO;
import cn.edu.sdu.qd.oj.contest.mapper.ContestDOMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ContestExtensionDao extends ServiceImpl<ContestDOMapper, ContestDO> {
}
