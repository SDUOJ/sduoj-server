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

import cn.edu.sdu.qd.oj.checkpoint.config.CheckpointFileSystemProperties;
import cn.edu.sdu.qd.oj.checkpoint.converter.CheckpointConverter;
import cn.edu.sdu.qd.oj.checkpoint.dao.CheckpointDao;
import cn.edu.sdu.qd.oj.checkpoint.entity.CheckpointDO;
import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.SnowflakeIdWorker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName CheckpointFileService
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/6 14:02
 * @Version V1.0
 **/

@Service
@EnableConfigurationProperties(CheckpointFileSystemProperties.class)
public class CheckpointFileService {

    @Autowired
    public CheckpointFileSystemProperties checkpointFileSystemProperties;

    // TODO: 临时采用 IP+PID 格式, 生产时加配置文件 Autowired
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Autowired
    private CheckpointDao checkpointDao;

    @Autowired
    private CheckpointConverter checkpointConverter;

    /**
     * @param files
     * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint[]
     * @Description 批量上传成对的测试点文件，如果不配对或者写入到文件系统中出现错误，则全部回滚
     **/
    @Transactional
    public List<CheckpointDTO> uploadCheckpointFiles(MultipartFile[] files) {
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
            for (MultipartFile input : files) {
                if ("in".equals(FilenameUtils.getExtension(input.getOriginalFilename()))) {
                    MultipartFile output = map.get(FilenameUtils.getBaseName(input.getOriginalFilename()) + ".out");
                    byte[] inputBytes = input.getBytes();
                    byte[] outputBytes = output.getBytes();
                    long snowflaskId = snowflakeIdWorker.nextId();
                    String snowflaskIdString = Long.toHexString(snowflaskId);
                    CheckpointDO checkpointDO = CheckpointDO.builder()
                            .checkpointId(snowflaskId)
                            .inputPreview(new String(inputBytes, 0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, inputBytes.length)))
                            .outputPreview(new String(outputBytes, 0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, outputBytes.length)))
                            .inputSize(inputBytes.length)
                            .outputSize(outputBytes.length)
                            .inputFilename(input.getOriginalFilename())
                            .outputFilename(output.getOriginalFilename())
                            .build();
                    checkpointDOList.add(checkpointDO);

                    File inputFile = new File(Paths.get(checkpointFileSystemProperties.getBaseDir(), snowflaskIdString + ".in").toString());
                    File outputFile = new File(Paths.get(checkpointFileSystemProperties.getBaseDir(), snowflaskIdString + ".out").toString());
                    FileUtils.writeByteArrayToFile(inputFile, inputBytes);
                    FileUtils.writeByteArrayToFile(outputFile, outputBytes);
                }
            }
            checkpointDao.saveBatch(checkpointDOList);
        } catch (Exception e) {
            for (CheckpointDO checkpointDO : checkpointDOList) {
                new File(Paths.get(checkpointFileSystemProperties.getBaseDir(), Long.toHexString(checkpointDO.getCheckpointId()) + ".in").toString()).delete();
                new File(Paths.get(checkpointFileSystemProperties.getBaseDir(), Long.toHexString(checkpointDO.getCheckpointId()) + ".out").toString()).delete();
            }
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
    public CheckpointDTO updateCheckpointFile(String input, String output) {
        long snowflaskId = snowflakeIdWorker.nextId();
        String snowflaskIdString = Long.toHexString(snowflaskId);
        File inputFile = new File(Paths.get(checkpointFileSystemProperties.getBaseDir(), snowflaskIdString + ".in").toString());
        File outputFile = new File(Paths.get(checkpointFileSystemProperties.getBaseDir(), snowflaskIdString + ".out").toString());
        CheckpointDO checkpointDO = CheckpointDO.builder()
                .checkpointId(snowflaskId)
                .inputPreview(input.substring(0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, input.length())))
                .outputPreview(output.substring(0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, output.length())))
                .inputSize(input.length())
                .outputSize(output.length())
                .build();
        try {
            checkpointDao.save(checkpointDO);
            FileUtils.writeStringToFile(inputFile, input);
            FileUtils.writeStringToFile(outputFile, output); 
        } catch (IOException e) {
            inputFile.delete();
            outputFile.delete();
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        return checkpointConverter.to(checkpointDO);
    }

    /**
    * @Description 向输出流写出 zip 文件
    * @param checkpointIds
    * @param zipOut
    * @return void
    **/
    public void downloadCheckpointFiles(List<String> checkpointIds, ZipOutputStream zipOut) throws IOException {
        // TODO: 文件安全性 校验
        List<FileSystemResource> files = new ArrayList<>();
        for (String checkpointId : checkpointIds) {
            FileSystemResource input = new FileSystemResource(Paths.get(checkpointFileSystemProperties.getBaseDir(), checkpointId + ".in").toString());
            FileSystemResource output = new FileSystemResource(Paths.get(checkpointFileSystemProperties.getBaseDir(), checkpointId + ".out").toString());
            AssertUtils.isTrue(input.exists() && output.exists(), ApiExceptionEnum.FILE_NOT_EXISTS);
            files.add(input);
            files.add(output);
        }
        for (FileSystemResource file : files) {
            ZipEntry zipEntry = new ZipEntry(file.getFilename());
            zipEntry.setSize(file.contentLength());
            zipOut.putNextEntry(zipEntry);
            StreamUtils.copy(file.getInputStream(), zipOut);
            zipOut.closeEntry();
        }
        zipOut.finish();
        zipOut.close();
    }

    /**
    * @Description 读取文件系统中的 checkpoint 内容
    * @param checkpointId
    **/
    public CheckpointDTO queryCheckpointFileContent(String checkpointId) throws IOException {

        File inputFile = new File(Paths.get(checkpointFileSystemProperties.getBaseDir(), checkpointId + ".in").toString());
        File outputFile = new File(Paths.get(checkpointFileSystemProperties.getBaseDir(), checkpointId + ".out").toString());

        AssertUtils.isTrue(inputFile.exists() && outputFile.exists(), ApiExceptionEnum.FILE_NOT_EXISTS);
        AssertUtils.isTrue(!(inputFile.length() > 1024*1024 || outputFile.length() > 1024*1024), ApiExceptionEnum.FILE_TOO_LARGE);

        String input = FileUtils.readFileToString(inputFile);
        String output = FileUtils.readFileToString(outputFile);

        return CheckpointDTO.builder()
                .checkpointId(Long.valueOf(checkpointId, 16))
                .input(input)
                .output(output)
                .build();
    }

}