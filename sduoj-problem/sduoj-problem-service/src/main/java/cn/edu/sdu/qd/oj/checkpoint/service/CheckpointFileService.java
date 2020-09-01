/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.service;

import cn.edu.sdu.qd.oj.checkpoint.config.CheckpointFileSystemProperties;
import cn.edu.sdu.qd.oj.checkpoint.mapper.CheckpointMapper;
import cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.file.Files;
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
    private CheckpointMapper checkpointMapper;

    /**
     * @param files
     * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint[]
     * @Description 批量上传成对的测试点文件，如果不配对或者写入到文件系统中出现错误，则全部回滚
     **/
    @Transactional
    public Checkpoint[] uploadCheckpointFiles(MultipartFile[] files) {
        List<Checkpoint> list = new ArrayList<>(files.length / 2 + 1);
        int n = 0;
        Arrays.sort(files, Comparator.comparing(MultipartFile::getOriginalFilename));
        Map<String, MultipartFile> map = new HashMap<>();
        for (MultipartFile file : files) {
            map.put(file.getOriginalFilename(), file);
        }
        // 检查文件是否配对
        for (MultipartFile input : files) {
            if ("in".equals(FilenameUtils.getExtension(input.getOriginalFilename()))) {
                MultipartFile output = map.get(FilenameUtils.getBaseName(input.getOriginalFilename()) + ".out");
                if (output == null) {
                    throw new ApiException(ApiExceptionEnum.FILE_NOT_DOUBLE);
                }
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
                    Checkpoint checkpoint = new Checkpoint(
                            snowflaskId,
                            new String(inputBytes, 0, Math.min(Checkpoint.MAX_DESCRIPTION_LENGTH, inputBytes.length)),
                            new String(outputBytes, 0, Math.min(Checkpoint.MAX_DESCRIPTION_LENGTH, outputBytes.length)),
                            inputBytes.length,
                            outputBytes.length,
                            input.getOriginalFilename(),
                            output.getOriginalFilename()
                    );
                    list.add(checkpoint);
                    File inputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskIdString + ".in");
                    File outputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskIdString + ".out");
                    FileUtils.writeByteArrayToFile(inputFile, inputBytes);
                    FileUtils.writeByteArrayToFile(outputFile, outputBytes);
                }
            }
            checkpointMapper.insertList(list);
        } catch (Exception e) {
            for (Checkpoint checkpoint : list) {
                new File(checkpointFileSystemProperties.getBaseDir() + File.separator + Long.toHexString(checkpoint.getCheckpointId()) + ".in").delete();
                new File(checkpointFileSystemProperties.getBaseDir() + File.separator + Long.toHexString(checkpoint.getCheckpointId()) + ".out").delete();
            }
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        return list.toArray(new Checkpoint[list.size()]);
    }


    /*
     * @Description 上传单对文本文件作为测试点文件
     * @param input
     * @param output
     * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint
     **/
    @Transactional
    public Checkpoint updateCheckpointFile(String input, String output) {
        long snowflaskId = snowflakeIdWorker.nextId();
        String snowflaskIdString = Long.toHexString(snowflaskId);
        File inputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskIdString + ".in");
        File outputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskIdString + ".out");
        Checkpoint checkpoint = new Checkpoint(
                snowflaskId,
                input.substring(0, Math.min(Checkpoint.MAX_DESCRIPTION_LENGTH, input.length())),
                output.substring(0, Math.min(Checkpoint.MAX_DESCRIPTION_LENGTH, output.length())),
                input.length(),
                output.length()
        );
        try {
            checkpointMapper.insert(checkpoint);
            FileUtils.writeStringToFile(inputFile, input);
            FileUtils.writeStringToFile(outputFile, output); 
        } catch (IOException e) {
            inputFile.delete();
            outputFile.delete();
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        return checkpoint;
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
            FileSystemResource input = new FileSystemResource(checkpointFileSystemProperties.getBaseDir() + File.separator + checkpointId + ".in");
            FileSystemResource output = new FileSystemResource(checkpointFileSystemProperties.getBaseDir() + File.separator + checkpointId + ".out");
            if (!input.exists() || !output.exists()) {
                throw new ApiException(ApiExceptionEnum.FILE_NOT_EXISTS);
            }
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
    * @return java.lang.String[] [0]:inputContent [1]:outputContent
    **/
    public String[] queryCheckpointFileContent(String checkpointId) throws IOException {
        File inputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + checkpointId + ".in");
        File outputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + checkpointId + ".out");
        if (!inputFile.exists() || !outputFile.exists()) {
            throw new ApiException(ApiExceptionEnum.FILE_NOT_EXISTS);
        }
        if (inputFile.length() > 1024*1024 || inputFile.length() > 1024*1024) {
            throw new ApiException(ApiExceptionEnum.FILE_TOO_LARGE);
        }
        return new String[]{FileUtils.readFileToString(inputFile), FileUtils.readFileToString(outputFile)};
    }

}