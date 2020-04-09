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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

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
    * @Description 批量上传成对的测试点文件，如果不配对或者写入到文件系统中出现错误，则全部回滚
    * @param files
    * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint[]
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
                if(output == null) {
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
                    File inputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskId + ".in");
                    File outputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskId + ".out");
                    FileUtils.writeByteArrayToFile(inputFile, inputBytes);
                    FileUtils.writeByteArrayToFile(outputFile, outputBytes);
                }
            }
            for(Checkpoint checkpoint : list) {
                checkpointMapper.insert(checkpoint);
            }
        } catch (Exception e) {
            for(Checkpoint checkpoint : list) {
                new File(checkpointFileSystemProperties.getBaseDir() + File.separator + checkpoint.getCheckpointId() + ".in").delete();
                new File(checkpointFileSystemProperties.getBaseDir() + File.separator + checkpoint.getCheckpointId() + ".out").delete();
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
        File inputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskId + ".in");
        File outputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskId + ".out");
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
}