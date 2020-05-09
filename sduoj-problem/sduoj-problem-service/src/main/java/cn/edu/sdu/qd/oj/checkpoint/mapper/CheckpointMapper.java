/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.mapper;

import cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint;
import cn.edu.sdu.qd.oj.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @ClassName checkpointMapper
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:28
 * @Version V1.0
 **/

public interface CheckpointMapper extends BaseMapper<Checkpoint, Long> {
}