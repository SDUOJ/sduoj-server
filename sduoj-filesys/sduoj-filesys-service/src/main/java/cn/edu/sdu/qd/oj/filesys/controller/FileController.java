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

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.dto.FileDTO;
import cn.edu.sdu.qd.oj.dto.FileDownloadReqDTO;
import cn.edu.sdu.qd.oj.filesys.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Slf4j
@Controller
@RequestMapping("/filesys")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    @ApiResponseBody
    public FileDTO upload(@RequestParam("file") @NotNull MultipartFile file) {
        return fileService.upload(file);
    }

    @PostMapping(value = "/uploadFiles", headers = "content-type=multipart/form-data")
    @ApiResponseBody
    public List<FileDTO> upload(@RequestParam("files") @NotNull MultipartFile[] files) {
        return fileService.uploadFiles(files);
    }

    @PostMapping(value = "/zipDownload")
    public void zipDownload(@RequestBody List<FileDownloadReqDTO> fileDownloadReqDTOList,
                            HttpServletResponse response) throws IOException {
        log.warn("zipDownload: {}", fileDownloadReqDTOList);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment"); // 前端定义文件名
        response.setContentType("application/zip; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        fileService.downloadFilesInZip(fileDownloadReqDTOList, new ZipOutputStream(response.getOutputStream()));
    }
}
