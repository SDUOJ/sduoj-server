/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.service;

import cn.edu.sdu.qd.oj.checkpoint.config.CheckpointFileSystemProperties;
import cn.edu.sdu.qd.oj.checkpoint.converter.CheckpointConverter;
import cn.edu.sdu.qd.oj.checkpoint.dao.CheckpointDao;
import cn.edu.sdu.qd.oj.checkpoint.entity.CheckpointDO;
import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
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
    public CheckpointDTO[] uploadCheckpointFiles(MultipartFile[] files) {
        List<CheckpointDTO> checkpointDTOList = new ArrayList<>(files.length / 2 + 1);
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
                    CheckpointDTO checkpointDTO = new CheckpointDTO(
                            snowflaskId,
                            new String(inputBytes, 0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, inputBytes.length)),
                            new String(outputBytes, 0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, outputBytes.length)),
                            inputBytes.length,
                            outputBytes.length,
                            input.getOriginalFilename(),
                            output.getOriginalFilename()
                    );
                    checkpointDTOList.add(checkpointDTO);
                    File inputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskIdString + ".in");
                    File outputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskIdString + ".out");
                    FileUtils.writeByteArrayToFile(inputFile, inputBytes);
                    FileUtils.writeByteArrayToFile(outputFile, outputBytes);
                }
            }
            List<CheckpointDO> checkpointDOList = checkpointConverter.from(checkpointDTOList);
            checkpointDao.saveBatch(checkpointDOList);
        } catch (Exception e) {
            for (CheckpointDTO checkpointDTO : checkpointDTOList) {
                new File(checkpointFileSystemProperties.getBaseDir() + File.separator + Long.toHexString(checkpointDTO.getCheckpointId()) + ".in").delete();
                new File(checkpointFileSystemProperties.getBaseDir() + File.separator + Long.toHexString(checkpointDTO.getCheckpointId()) + ".out").delete();
            }
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        return checkpointDTOList.toArray(new CheckpointDTO[0]);
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
        File inputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskIdString + ".in");
        File outputFile = new File(checkpointFileSystemProperties.getBaseDir() + File.separator + snowflaskIdString + ".out");
        CheckpointDTO checkpointDTO = new CheckpointDTO(
                snowflaskId,
                input.substring(0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, input.length())),
                output.substring(0, Math.min(CheckpointDTO.MAX_DESCRIPTION_LENGTH, output.length())),
                input.length(),
                output.length()
        );
        try {
            CheckpointDO checkpointDO = checkpointConverter.from(checkpointDTO);
            checkpointDao.save(checkpointDO);
            FileUtils.writeStringToFile(inputFile, input);
            FileUtils.writeStringToFile(outputFile, output); 
        } catch (IOException e) {
            inputFile.delete();
            outputFile.delete();
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        return checkpointDTO;
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