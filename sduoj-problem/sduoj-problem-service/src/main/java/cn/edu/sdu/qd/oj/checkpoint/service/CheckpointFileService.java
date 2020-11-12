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

import cn.edu.sdu.qd.oj.checkpoint.client.FilesysClient;
import cn.edu.sdu.qd.oj.checkpoint.converter.CheckpointConverter;
import cn.edu.sdu.qd.oj.checkpoint.dao.CheckpointDao;
import cn.edu.sdu.qd.oj.checkpoint.entity.CheckpointDO;
import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.SnowflakeIdWorker;
import cn.edu.sdu.qd.oj.dto.BinaryFileUploadReqDTO;
import cn.edu.sdu.qd.oj.dto.FileDTO;
import cn.edu.sdu.qd.oj.dto.PlainFileDownloadDTO;
import com.alibaba.nacos.common.utils.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName CheckpointFileService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/6 14:02
 * @Version V1.0
 **/

@Slf4j
@Service
public class CheckpointFileService {

    // TODO: 临时采用 IP+PID 格式, 生产时加配置文件 Autowired
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Autowired
    private CheckpointDao checkpointDao;

    @Autowired
    private CheckpointConverter checkpointConverter;

    @Autowired
    private FilesysClient filesysClient;

    /**
     * @param files
     * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint[]
     * @Description 批量上传成对的测试点文件，如果不配对或者写入到文件系统中出现错误，则全部回滚
     **/
    @Transactional
    public List<CheckpointDTO> uploadCheckpointFiles(MultipartFile[] files, Long userId) {
        List<CheckpointDO> checkpointDOList = new ArrayList<>(files.length / 2 + 1);
        Map<String, MultipartFile> map = new HashMap<>();
        for (MultipartFile file : files) {
            map.put(file.getOriginalFilename(), file);
        }
        // 检查文件是否配对
        for (MultipartFile input : files) {
            if ("in".equals(FilenameUtils.getExtension(input.getOriginalFilename()))) {
                MultipartFile output = map.get(FilenameUtils.getBaseName(input.getOriginalFilename()) + ".out");
                AssertUtils.notNull(output, ApiExceptionEnum.FILE_NOT_DOUBLE);
            }
        }
        // 开始写入文件
        try {
            // 调用文件系统api，上传文件
            List<BinaryFileUploadReqDTO> uploadReqDTOList = new ArrayList<>(files.length);
            for (MultipartFile file : files) {
                byte[] bytes = file.getBytes();
                uploadReqDTOList.add(
                    BinaryFileUploadReqDTO.builder().bytes(bytes).size((long) bytes.length).filename(file.getOriginalFilename()).build()
                );
            }

            List<FileDTO> fileDTOList = filesysClient.uploadBinaryFiles(uploadReqDTOList, userId);
            Map<String, FileDTO> fileDTOMap = fileDTOList.stream().collect(Collectors.toMap(FileDTO::getName, Function.identity()));

            // 整理成 checkpointDO 存在本微服务数据库
            for (MultipartFile input : files) {
                if ("in".equals(FilenameUtils.getExtension(input.getOriginalFilename()))) {
                    MultipartFile output = map.get(FilenameUtils.getBaseName(input.getOriginalFilename()) + ".out");

                    FileDTO inputFileDTO = fileDTOMap.get(input.getOriginalFilename());
                    FileDTO outputFileDTO = fileDTOMap.get(output.getOriginalFilename());

                    byte[] inputBytes = input.getBytes();
                    byte[] outputBytes = output.getBytes();
                    long snowflaskId = snowflakeIdWorker.nextId();
                    CheckpointDO checkpointDO = CheckpointDO.builder()
                            .checkpointId(snowflaskId)
                            .inputPreview(new String(inputBytes, 0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, inputBytes.length)))
                            .outputPreview(new String(outputBytes, 0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, outputBytes.length)))
                            .inputSize(inputBytes.length)
                            .outputSize(outputBytes.length)
                            .inputFilename(input.getOriginalFilename())
                            .outputFilename(output.getOriginalFilename())
                            .inputFileId(inputFileDTO.getId())
                            .outputFileId(outputFileDTO.getId())
                            .build();
                    checkpointDOList.add(checkpointDO);
                }
            }
            checkpointDao.saveBatch(checkpointDOList);
        } catch (Exception e) {
            log.error("{}", e);
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        return checkpointConverter.to(checkpointDOList);
    }


    /*
     * @Description 上传单对文本文件作为测试点文件
     * @param input
     * @param output
     * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint
     **/
    @Transactional
    public CheckpointDTO updateCheckpointFile(String input, String output, Long userId) {
        long snowflaskId = snowflakeIdWorker.nextId();

        byte[] inputBytes = input.getBytes();
        byte[] outputBytes = output.getBytes();
        List<BinaryFileUploadReqDTO> uploadReqDTOList = Lists.newArrayList(
                BinaryFileUploadReqDTO.builder().bytes(inputBytes).size((long) inputBytes.length).filename(snowflaskId + ".in").build(),
                BinaryFileUploadReqDTO.builder().bytes(outputBytes).size((long) outputBytes.length).filename(snowflaskId + ".out").build()
        );
        String inputMd5 = Md5Utils.getMD5(inputBytes);
        String outputMd5 = Md5Utils.getMD5(outputBytes);

        CheckpointDO checkpointDO = CheckpointDO.builder()
                .checkpointId(snowflaskId)
                .inputPreview(input.substring(0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, input.length())))
                .outputPreview(output.substring(0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, output.length())))
                .inputSize(input.length())
                .outputSize(output.length())
                .build();
        try {
            List<FileDTO> fileDTOList = filesysClient.uploadBinaryFiles(uploadReqDTOList, userId);
            Map<String, FileDTO> fileDTOMap = fileDTOList.stream().collect(Collectors.toMap(FileDTO::getMd5, Function.identity()));
            checkpointDO.setInputFileId(fileDTOMap.get(inputMd5).getId());
            checkpointDO.setOutputFileId(fileDTOMap.get(outputMd5).getId());
            checkpointDao.save(checkpointDO);
        } catch (Exception e) {
            log.error("{}", e);
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        return checkpointConverter.to(checkpointDO);
    }

    /**
    * @Description 读取文件系统中的 checkpoint 内容
    * @param checkpointId
    **/
    public CheckpointDTO queryCheckpointFileContent(Long checkpointId) throws IOException {
        CheckpointDO checkpointDO = checkpointDao.getById(checkpointId);

        AssertUtils.notNull(checkpointDO, ApiExceptionEnum.FILE_NOT_EXISTS);
        AssertUtils.isTrue(!(checkpointDO.getInputSize() > 1024 * 1024 || checkpointDO.getOutputSize() > 1024 * 1024), ApiExceptionEnum.FILE_TOO_LARGE);

        List<PlainFileDownloadDTO> downloadDTOList = filesysClient.plainFileDownload(1024 * 1024L,
                Lists.newArrayList(PlainFileDownloadDTO.builder().fileId(checkpointDO.getInputFileId()).build(),
                        PlainFileDownloadDTO.builder().fileId(checkpointDO.getOutputFileId()).build())
        );
        AssertUtils.isTrue(downloadDTOList != null && downloadDTOList.size() == 2, ApiExceptionEnum.SERVER_BUSY);

        return CheckpointDTO.builder()
                .checkpointId(checkpointId)
                .input(new String(downloadDTOList.get(0).getBytes()))
                .output(new String(downloadDTOList.get(1).getBytes()))
                .build();
    }

}