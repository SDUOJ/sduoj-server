package cn.edu.sdu.qd.oj.problem.dao;

import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemDOMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ProblemDao extends ServiceImpl<ProblemDOMapper, ProblemDO> {
}
