/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.filesys.service.impl;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.common.util.CodecUtils;
import cn.edu.sdu.qd.oj.common.util.SnowflakeIdWorker;
import cn.edu.sdu.qd.oj.dto.BinaryFileUploadReqDTO;
import cn.edu.sdu.qd.oj.dto.FileDTO;
import cn.edu.sdu.qd.oj.dto.FileDownloadReqDTO;
import cn.edu.sdu.qd.oj.filesys.config.FileSystemProperties;
import cn.edu.sdu.qd.oj.filesys.converter.FileConverter;
import cn.edu.sdu.qd.oj.filesys.dao.FileDao;
import cn.edu.sdu.qd.oj.filesys.entity.FileDO;
import cn.edu.sdu.qd.oj.filesys.service.FileService;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@EnableConfigurationProperties(FileSystemProperties.class)
public class LocalFileService implements FileService {

    @Autowired
    private FileSystemProperties fileSystemProperties;

    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Autowired
    private FileDao fileDao;

    @Autowired
    private FileConverter fileConverter;

    @Transactional
    @Override
    public FileDTO upload(MultipartFile file) {
        // 计算文件 md5
        String md5;
        try {
            md5 = CodecUtils.md5(file.getInputStream());
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        // 查询是否已有相同 MD5
        FileDO fileDO = fileDao.lambdaQuery().eq(FileDO::getMd5, md5).one();
        if (fileDO != null) {
            return fileConverter.to(fileDO);
        }

        Long id = snowflakeIdWorker.nextId();
        fileDO = FileDO.builder()
                .id(id)
                .name(file.getOriginalFilename())
                .extensionName(Files.getFileExtension(file.getOriginalFilename()))
                .md5(md5)
                .size(file.getSize())
                .build();
        AssertUtils.isTrue(fileDao.save(fileDO), ApiExceptionEnum.SERVER_BUSY);
        try {
            File writeFile = new File(Paths.get(fileSystemProperties.getBaseDir(), id.toString()).toString());
            byte[] bytes = file.getBytes();
            FileUtils.writeByteArrayToFile(writeFile, bytes);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        return fileConverter.to(fileDO);
    }

    @Override
    @Transactional
    public List<FileDTO> uploadFiles(MultipartFile[] files) {
        List<BinaryFileUploadReqDTO> reqDTOList = new ArrayList<>(files.length);
        try {
            for (MultipartFile file : files) {
                byte[] bytes = file.getBytes();
                reqDTOList.add(
                    BinaryFileUploadReqDTO.builder()
                        .filename(file.getOriginalFilename())
                        .bytes(bytes)
                        .size((long) bytes.length)
                        .inputStream(file.getInputStream())
                        .build()
                );
            }
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        return uploadBinaryFiles(reqDTOList);
    }

    @Override
    public void downloadFilesInZip(List<FileDownloadReqDTO> fileDownloadReqDTOList, ZipOutputStream zipOut) {
        for (FileDownloadReqDTO fileDownloadReqDTO : fileDownloadReqDTOList) {
            FileSystemResource file = new FileSystemResource(Paths.get(fileSystemProperties.getBaseDir(), fileDownloadReqDTO.getId().toString()).toString());
            AssertUtils.isTrue(file.exists(), ApiExceptionEnum.FILE_NOT_EXISTS);
            fileDownloadReqDTO.setFileSystemResource(file);
        }
        try {
            for (FileDownloadReqDTO fileDownloadReqDTO : fileDownloadReqDTOList) {
                FileSystemResource file = fileDownloadReqDTO.getFileSystemResource();
                ZipEntry zipEntry = new ZipEntry(fileDownloadReqDTO.getDownloadFilename());
                zipEntry.setSize(file.contentLength());
                zipOut.putNextEntry(zipEntry);
                StreamUtils.copy(file.getInputStream(), zipOut);
                zipOut.closeEntry();
            }
            zipOut.finish();
            zipOut.close();
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.FILE_READ_ERROR);
        }
    }

    @Override
    public byte[] downloadFilesInZipBytes(List<FileDownloadReqDTO> fileDownloadReqDTOList) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        downloadFilesInZip(fileDownloadReqDTOList, zipOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    @Transactional
    public List<FileDTO> uploadBinaryFiles(List<BinaryFileUploadReqDTO> reqDTOList) {
        String[] md5s = new String[reqDTOList.size()];
        // 计算文件 md5
        try {
            for (int i = 0, n = reqDTOList.size(); i < n; i++) {
                md5s[i] = CodecUtils.md5(reqDTOList.get(i).getBytes());
            }
        } catch (Exception e) {
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        // 查询是否已有相同 MD5
        List<FileDO> fileDOList = fileDao.lambdaQuery().in(FileDO::getMd5, md5s).list();
        if (fileDOList.size() == reqDTOList.size()) {
            return fileConverter.to(fileDOList);
        }

        Map<String, FileDO> fileDOMap = fileDOList.stream().collect(Collectors.toMap(FileDO::getMd5, Function.identity()));

        List<FileDO> newFileDOList = new ArrayList<>();
        for (int i = 0, n = reqDTOList.size(); i < n; i++) {
            FileDO fileDO = fileDOMap.get(md5s[i]);
            if (fileDO == null) {
                fileDO = FileDO.builder()
                        .id(snowflakeIdWorker.nextId())
                        .name(reqDTOList.get(i).getFilename())
                        .extensionName(Files.getFileExtension(reqDTOList.get(i).getFilename()))
                        .md5(md5s[i])
                        .size(reqDTOList.get(i).getSize())
                        .build();
                newFileDOList.add(fileDO);
                fileDOMap.put(fileDO.getMd5(), fileDO);
            }
        }
        AssertUtils.isTrue(fileDao.saveBatch(newFileDOList), ApiExceptionEnum.SERVER_BUSY);

        int i = 0;
        try {
            for (int n = reqDTOList.size(); i < n; i++) {
                FileDO fileDO = fileDOMap.get(md5s[i]);
                File writeFile = new File(Paths.get(fileSystemProperties.getBaseDir(), fileDO.getId().toString()).toString());
                byte[] bytes = reqDTOList.get(i).getBytes();
                FileUtils.writeByteArrayToFile(writeFile, bytes);
            }
        } catch (Exception e) {
            for (int j = 0; j <= i; j++) {
                FileDO fileDO = fileDOMap.get(md5s[j]);
                new File(Paths.get(fileSystemProperties.getBaseDir(), fileDO.getId().toString()).toString()).delete();
            }
            throw new ApiException(ApiExceptionEnum.FILE_WRITE_ERROR);
        }
        fileDOList.addAll(newFileDOList);
        return fileConverter.to(fileDOList);
    }
}