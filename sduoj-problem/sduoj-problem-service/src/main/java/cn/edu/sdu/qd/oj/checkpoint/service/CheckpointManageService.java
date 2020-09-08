/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.service;

import cn.edu.sdu.qd.oj.checkpoint.entity.CheckpointDO;
import cn.edu.sdu.qd.oj.checkpoint.mapper.CheckpointDOMapper;
import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointDTO;
import cn.edu.sdu.qd.oj.problem.mapper.ProblemManageDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

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
    private CheckpointDOMapper checkpointDOMapper;

    @Autowired
    private ProblemManageDOMapper problemManageDOMapper;

    public List<CheckpointDTO> getCheckpoints(int problemId) {
        List<Map> list = problemManageDOMapper.queryCheckpointIds(problemId);
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
        List<CheckpointDO> checkpointDOList = checkpointDOMapper.selectByIdList(checkpointIds);
        List<CheckpointDTO> checkpointDTOList = checkpointDOList.stream().map(checkpointDO -> CheckpointDTO.builder()
                .checkpointId(checkpointDO.getCheckpointId())
                .inputDescription(checkpointDO.getInputDescription())
                .inputFileName(checkpointDO.getInputFileName())
                .inputSize(checkpointDO.getInputSize())
                .outputDescription(checkpointDO.getOutputDescription())
                .outputFileName(checkpointDO.getOutputFileName())
                .outputSize(checkpointDO.getOutputSize())
                .build()).collect(Collectors.toList());
        checkpointDTOList.sort(Comparator.comparing(o -> indexMap.get(o.getCheckpointId())));
        return checkpointDTOList;
    }
}