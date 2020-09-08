/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.mapper;

import cn.edu.sdu.qd.oj.problem.entity.ProblemManageDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/1 19:52
 * @Version V1.0
 **/

public interface ProblemManageDOMapper extends Mapper<ProblemManageDO> {
    @Select("SELECT p_checkpoint_ids FROM oj_problems WHERE p_id=#{problemId}")
    public List<Map> queryCheckpointIds(@Param("problemId") int problemId);
}