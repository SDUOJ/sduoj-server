/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.filesys.controller;

import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import cn.edu.sdu.qd.oj.filesys.dto.FileDTO;
import cn.edu.sdu.qd.oj.filesys.dto.FileDownloadReqDTO;
import cn.edu.sdu.qd.oj.filesys.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public FileDTO upload(@RequestParam("file") @NotNull MultipartFile file,
                          @UserSession UserSessionDTO userSessionDTO) {
        return fileService.upload(file, userSessionDTO.getUserId());
    }

    @PostMapping(value = "/uploadFiles", headers = "content-type=multipart/form-data")
    @ApiResponseBody
    public List<FileDTO> upload(@RequestParam("files") @NotNull MultipartFile[] files,
                                @UserSession UserSessionDTO userSessionDTO) {
        return fileService.uploadFiles(files, userSessionDTO.getUserId());
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

    /**
    * @Description 以 filename 为文件名下载指定 fileId 的文件
    **/
    @GetMapping(value = "/download/{fileId}/{filename}")
    public void download(@PathVariable("fileId") long fileId,
                         @PathVariable("filename") String filename,
                         HttpServletResponse response) throws IOException {
        log.info("download: {}", fileId);

        if ("source".equals(filename)) {
            filename = fileService.fileIdToFilename(fileId);
            AssertUtils.notNull(filename, ApiExceptionEnum.FILE_NOT_EXISTS);
        }

        // 从 filename 获取 contentType
        String contentType = null;
        try {
            contentType = Files.probeContentType(Paths.get(filename)); // 这个 API 最终调用 JNI, 结果可能因 OS 而不同
        } catch (Exception e) {
            log.warn("", e);
        }

        // 设置 response
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentType(contentType != null ? contentType : "application/octet-stream; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // 读取文件
        try {
            fileService.downloadToStream(fileId, response.getOutputStream());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping(value = "/queryByMd5")
    @ApiResponseBody
    public FileDTO queryByMd5(@RequestParam("md5") String md5) {
        return fileService.queryByMd5(md5);
    }
}
