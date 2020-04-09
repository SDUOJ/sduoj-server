package cn.edu.sdu.qd.oj.problem.mapper;

import cn.edu.sdu.qd.oj.problem.pojo.Problem;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface ProblemMapper extends Mapper<Problem> {

    @Select("SELECT p_id,p_title FROM oj_problems")
    public List<Map> queryIdToTitleMap();
}
