/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.service;

import cn.edu.sdu.qd.oj.checkpoint.converter.CheckpointConverter;
import cn.edu.sdu.qd.oj.checkpoint.dao.CheckpointDao;
import cn.edu.sdu.qd.oj.checkpoint.entity.CheckpointDO;
import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointDTO;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private CheckpointDao checkpointDao;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private CheckpointConverter checkpointConverter;

    public List<CheckpointDTO> getCheckpoints(String problemCode) {
        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getCheckpoints
        ).eq(ProblemDO::getProblemCode, problemCode).one();
        byte[] bytes = Optional.ofNullable(problemDO).map(ProblemDO::getCheckpoints).orElse(null);
        if (bytes == null || bytes.length == 0) {
            return new ArrayList<>();
        }
        List<Long> checkpointIds = new ArrayList<>(bytes.length / 8);
        Map<Long, Integer> indexMap = new HashMap<>(bytes.length / 8);
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        for (int i = 0, size = bytes.length; i < size; i += 8) {
            checkpointIds.add(wrap.getLong(i));
            indexMap.put(wrap.getLong(i), i);
        }
        List<CheckpointDO> checkpointDOList = checkpointDao.listByIds(checkpointIds);
        List<CheckpointDTO> checkpointDTOList = checkpointConverter.to(checkpointDOList);
        checkpointDTOList.sort(Comparator.comparing(o -> indexMap.get(o.getCheckpointId())));
        return checkpointDTOList;
    }
}