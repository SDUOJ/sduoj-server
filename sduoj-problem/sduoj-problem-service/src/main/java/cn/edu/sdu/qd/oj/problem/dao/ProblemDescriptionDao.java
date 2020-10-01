package cn.edu.sdu.qd.oj.problem.dao;

import cn.edu.sdu.qd.oj.problem.entity.ProblemDescriptionDO;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemDescriptionDOMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ProblemDescriptionDao extends ServiceImpl<ProblemDescriptionDOMapper, ProblemDescriptionDO> {
}
