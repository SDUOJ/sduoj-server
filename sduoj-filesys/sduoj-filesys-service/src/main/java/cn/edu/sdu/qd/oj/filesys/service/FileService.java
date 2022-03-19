/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.filesys.service;

import cn.edu.sdu.qd.oj.filesys.dto.BinaryFileUploadReqDTO;
import cn.edu.sdu.qd.oj.filesys.dto.FileDTO;
import cn.edu.sdu.qd.oj.filesys.dto.FileDownloadReqDTO;
import cn.edu.sdu.qd.oj.filesys.dto.PlainFileDownloadDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

public interface FileService {

    /**
    * @Description 上传单文件
    **/
    FileDTO upload(MultipartFile file, Long userId);

    /**
    * @Description 批量上传文件
    **/
    List<FileDTO> uploadFiles(MultipartFile[] files, Long usedId);

    /**
     * @Description 以ZIP包形式下载多个文件
     **/
    void downloadFilesInZip(List<FileDownloadReqDTO> fileDownloadReqDTOList, ZipOutputStream zipOut);

    /**
     * @Description 以ZIP包字节流形式下载多个文件
     **/
    byte[] downloadFilesInZipBytes(List<FileDownloadReqDTO> fileDownloadReqDTOList);

    /**
     * @Description 批量上传二进制文件
     **/
    List<FileDTO> uploadBinaryFiles(List<BinaryFileUploadReqDTO> reqDTOList, Long userId);

    /**
     * @Description 文本文件序列化形式下载
     **/
    List<PlainFileDownloadDTO> plainFileDownload(Long sizeLimit, List<PlainFileDownloadDTO> reqDTOList);

    /**
    * @Description 根据MD5查询相关文件信息
    **/
    FileDTO queryByMd5(String md5);

    /**
    * @Description 直接获取文件二进制流
    **/
    byte[] downloadFile(long id) throws IOException;

    /**
    * @Description 对输出流写出文件二进制
    **/
    void downloadToStream(long fileId, OutputStream outputStream) throws IOException;

    /**
    * @Description 根据fileId查源文件名
    **/
    String fileIdToFilename(long fileId);
}
