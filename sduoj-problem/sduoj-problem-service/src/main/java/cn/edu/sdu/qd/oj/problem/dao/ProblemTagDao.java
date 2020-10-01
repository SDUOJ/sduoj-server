package cn.edu.sdu.qd.oj.problem.dao;

import cn.edu.sdu.qd.oj.problem.entity.ProblemTagDO;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemTagDOMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ProblemTagDao extends ServiceImpl<ProblemTagDOMapper, ProblemTagDO> {
}
