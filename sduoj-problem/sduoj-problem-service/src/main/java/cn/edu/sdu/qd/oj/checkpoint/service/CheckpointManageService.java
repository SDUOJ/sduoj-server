/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

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


    private List<CheckpointDTO> getCheckpoints(ProblemDO problemDO) {
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

    public List<CheckpointDTO> getCheckpoints(String problemCode) {
        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getCheckpoints
        ).eq(ProblemDO::getProblemCode, problemCode).one();
        return getCheckpoints(problemDO);
    }

    public List<CheckpointDTO> getCheckpoints(Long problemId) {
        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getCheckpoints
        ).eq(ProblemDO::getProblemId, problemId).one();
        return getCheckpoints(problemDO);
    }

}