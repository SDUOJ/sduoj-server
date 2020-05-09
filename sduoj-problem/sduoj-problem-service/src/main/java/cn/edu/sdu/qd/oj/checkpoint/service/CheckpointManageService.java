/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.service;

import cn.edu.sdu.qd.oj.checkpoint.mapper.CheckpointMapper;
import cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemManageBoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @ClassName checkpointManageService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:28
 * @Version V1.0
 **/

@Service
public class CheckpointManageService {

    @Autowired
    private CheckpointMapper checkpointMapper;

    @Autowired
    private ProblemManageBoMapper problemManageBoMapper;

    public List<Checkpoint> getCheckpoints(int problemId) {
        List<Map> list = problemManageBoMapper.queryCheckpointIds(problemId);
        byte[] bytes = (byte[]) list.get(0).get("p_checkpoint_ids");
        if (bytes.length == 0) {
            return new ArrayList<>();
        }
        List<Long> checkpointIds = new ArrayList<>(bytes.length / 8);
        Map<Long, Integer> indexMap = new HashMap<>(bytes.length / 8);
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        for (int i = 0, size = bytes.length; i < size; i += 8) {
            checkpointIds.add(wrap.getLong(i));
            indexMap.put(wrap.getLong(i), i);
        }
        List<Checkpoint> checkpoints = checkpointMapper.selectByIdList(checkpointIds);
        checkpoints.sort(Comparator.comparing(o -> indexMap.get(o.getCheckpointId())));
        return checkpoints;
    }
}