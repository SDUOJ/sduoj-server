/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.service;

import cn.edu.sdu.qd.oj.checkpoint.client.FilesysClient;
import cn.edu.sdu.qd.oj.checkpoint.converter.CheckpointConverter;
import cn.edu.sdu.qd.oj.checkpoint.converter.CheckpointManageListConverter;
import cn.edu.sdu.qd.oj.checkpoint.dao.CheckpointDao;
import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointDTO;
import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointManageListDTO;
import cn.edu.sdu.qd.oj.checkpoint.entity.CheckpointDO;
import cn.edu.sdu.qd.oj.dto.PlainFileDownloadDTO;
import cn.edu.sdu.qd.oj.problem.converter.ProblemConverterUtils;
import cn.edu.sdu.qd.oj.problem.dao.ProblemDao;
import cn.edu.sdu.qd.oj.problem.dto.ProblemCheckpointDTO;
import cn.edu.sdu.qd.oj.problem.entity.ProblemDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
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
    private CheckpointDao checkpointDao;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private FilesysClient filesysClient;

    @Autowired
    private CheckpointConverter checkpointConverter;

    @Autowired
    private CheckpointManageListConverter checkpointManageListConverter;

    /**
    * @Description
    * @param problemDO
    * @return java.util.List<cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointManageListDTO>
    **/
    private List<CheckpointManageListDTO> getCheckpoints(ProblemDO problemDO) {
        // 查询 problem
        List<ProblemCheckpointDTO> problemCheckpointDTOList = Optional.ofNullable(problemDO)
                .map(ProblemDO::getCheckpoints).map(ProblemConverterUtils::checkpointsTo).orElse(null);
        if (CollectionUtils.isEmpty(problemCheckpointDTOList)) {
            return new ArrayList<>();
        }
        // 查询 checkpoint
        List<Long> checkpointIds = problemCheckpointDTOList.stream().map(ProblemCheckpointDTO::getCheckpointId).collect(Collectors.toList());
        List<CheckpointDO> checkpointDOList = checkpointDao.listByIds(checkpointIds);
        List<CheckpointManageListDTO> checkpointDTOList = checkpointManageListConverter.to(checkpointDOList);
        // 排序
        Map<Long, Integer> idToIndex = cn.edu.sdu.qd.oj.common.util.CollectionUtils.getMapToIndex(checkpointIds);
        checkpointDTOList.sort(Comparator.comparing(o -> idToIndex.get(o.getCheckpointId())));
        // 填补数据 checkpointScore
        Map<Long, ProblemCheckpointDTO> problemCheckpointDTOMap = problemCheckpointDTOList.stream()
                .collect(Collectors.toMap(ProblemCheckpointDTO::getCheckpointId, Function.identity(), (k1, k2) -> k1));
        checkpointDTOList.forEach(o -> {
            o.setCheckpointScore(problemCheckpointDTOMap.get(o.getCheckpointId()).getCheckpointScore());
        });
        // 增补数据 caseIndex
        List<Long> caseIdList = Optional.ofNullable(problemDO)
                .map(ProblemDO::getCheckpointCases).map(ProblemConverterUtils::bytesToLongList).orElse(null);
        Map<Long, Integer> caseIdToIndex = cn.edu.sdu.qd.oj.common.util.CollectionUtils.getMapToIndex(caseIdList);
        checkpointDTOList.forEach(o -> {
            o.setCaseIndex(caseIdToIndex.get(o.getCheckpointId()));
        });
        return checkpointDTOList;
    }

    public List<CheckpointManageListDTO> getCheckpoints(String problemCode) {
        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getCheckpoints,
                ProblemDO::getCheckpointCases
        ).eq(ProblemDO::getProblemCode, problemCode).one();
        return getCheckpoints(problemDO);
    }

    public List<CheckpointManageListDTO> getCheckpoints(Long problemId) {
        ProblemDO problemDO = problemDao.lambdaQuery().select(
                ProblemDO::getProblemId,
                ProblemDO::getCheckpoints,
                ProblemDO::getCheckpointCases
        ).eq(ProblemDO::getProblemId, problemId).one();
        return getCheckpoints(problemDO);
    }

    public List<CheckpointDTO> listByIdList(List<Long> checkpointIdList) {
        List<CheckpointDO> checkpointDOList = checkpointDao.lambdaQuery().select(
            CheckpointDO::getCheckpointId,
            CheckpointDO::getInputFileId,
            CheckpointDO::getOutputFileId
        ).in(CheckpointDO::getCheckpointId, checkpointIdList).list();
        List<PlainFileDownloadDTO> downloadDTOList = new ArrayList<>(checkpointDOList.size() * 2);
        checkpointDOList.forEach(o -> {
            downloadDTOList.add(PlainFileDownloadDTO.builder().fileId(o.getInputFileId()).build());
            downloadDTOList.add(PlainFileDownloadDTO.builder().fileId(o.getOutputFileId()).build());
        });
        // TODO: 魔法值解决
        List<PlainFileDownloadDTO> checkpointDownloadRespList = filesysClient.plainFileDownload(1024L, downloadDTOList);
        Map<Long, byte[]> fileIdToBytes = checkpointDownloadRespList.stream().collect(Collectors.toMap(PlainFileDownloadDTO::getFileId, PlainFileDownloadDTO::getBytes, (k1, k2) -> k1));
        checkpointDOList.forEach(o -> {
            o.setInputPreview(new String(fileIdToBytes.get(o.getInputFileId())));
            o.setOutputPreview(new String(fileIdToBytes.get(o.getOutputFileId())));
        });
        return checkpointConverter.to(checkpointDOList);
    }

}