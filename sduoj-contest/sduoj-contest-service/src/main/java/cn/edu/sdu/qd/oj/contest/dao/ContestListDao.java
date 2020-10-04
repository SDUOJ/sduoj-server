package cn.edu.sdu.qd.oj.contest.dao;

import cn.edu.sdu.qd.oj.contest.entity.ContestListDO;
import cn.edu.sdu.qd.oj.contest.mapper.ContestListDOMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ContestListDao extends ServiceImpl<ContestListDOMapper, ContestListDO> {
}
