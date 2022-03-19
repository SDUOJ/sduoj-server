/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.filesys.api;

import cn.edu.sdu.qd.oj.filesys.dto.BinaryFileUploadReqDTO;
import cn.edu.sdu.qd.oj.filesys.dto.FileDTO;
import cn.edu.sdu.qd.oj.filesys.dto.FileDownloadReqDTO;
import cn.edu.sdu.qd.oj.filesys.dto.PlainFileDownloadDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @InterfaceName FilesysApi
 * @Author zhangt2333
 * @Date 2020/10/20
 **/

@RequestMapping("/internal/filesys")
public interface FilesysApi {
    String SERVICE_NAME = "filesys-service";

    @PostMapping(value = "/uploadBinaryFiles", consumes = "application/json")
    List<FileDTO> uploadBinaryFiles(@RequestBody List<BinaryFileUploadReqDTO> reqDTOList,
                                    @RequestParam("userId") long userId);

    @GetMapping(value = "/download", headers = "content-type=application/json")
    Resource download(@RequestParam("id") long id) throws IOException;

    @PostMapping(value = "/zipDownload", headers = "content-type=application/json")
    Resource download(@RequestBody List<FileDownloadReqDTO> reqDTOList);

    @PostMapping(value = "/plainFileDownload", headers = "content-type=application/json")
    List<PlainFileDownloadDTO> plainFileDownload(@RequestParam("sizeLimit") Long sizeLimit,
                                                 @RequestBody List<PlainFileDownloadDTO> reqDTOList); // 限制下载文件的大小
}