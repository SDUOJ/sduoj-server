package cn.edu.sdu.qd.oj.problem.mapper;

import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface ProblemDOMapper extends Mapper<ProblemDO> {

    @Select("SELECT p_id,p_title,p_checkpoint_num FROM oj_problems")
    public List<Map> queryIdToRedisHash();
}
