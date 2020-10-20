/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.filesys.controller;


import cn.edu.sdu.qd.oj.api.FilesysApi;
import cn.edu.sdu.qd.oj.dto.BinaryFileUploadReqDTO;
import cn.edu.sdu.qd.oj.dto.FileDTO;
import cn.edu.sdu.qd.oj.dto.FileDownloadReqDTO;
import cn.edu.sdu.qd.oj.filesys.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FileInternalController implements FilesysApi {

    @Autowired
    private FileService fileService;

    @Override
    public List<FileDTO> uploadBinaryFiles(List<BinaryFileUploadReqDTO> reqDTOList) {
        return fileService.uploadBinaryFiles(reqDTOList);
    }

    @Override
    public Resource download(List<FileDownloadReqDTO> reqDTOList) {
        return new ByteArrayResource(fileService.downloadFilesInZipBytes(reqDTOList));
    }
}
