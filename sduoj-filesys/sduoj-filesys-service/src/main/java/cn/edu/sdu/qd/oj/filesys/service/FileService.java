/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.filesys.service;

import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.dto.BinaryFileUploadReqDTO;
import cn.edu.sdu.qd.oj.dto.FileDTO;
import cn.edu.sdu.qd.oj.dto.FileDownloadReqDTO;
import cn.edu.sdu.qd.oj.dto.PlainFileDownloadDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.zip.ZipOutputStream;

public interface FileService {

    FileDTO upload(MultipartFile file);

    List<FileDTO> uploadFiles(MultipartFile[] files, Long usedId);

    void downloadFilesInZip(List<FileDownloadReqDTO> fileDownloadReqDTOList, ZipOutputStream zipOut);

    byte[] downloadFilesInZipBytes(List<FileDownloadReqDTO> fileDownloadReqDTOList);

    List<FileDTO> uploadBinaryFiles(List<BinaryFileUploadReqDTO> reqDTOList, Long userId);

    List<PlainFileDownloadDTO> plainFileDownload(Long sizeLimit, List<PlainFileDownloadDTO> reqDTOList);

    FileDTO queryByMd5(String md5);
}
