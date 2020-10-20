/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.api;

import cn.edu.sdu.qd.oj.dto.BinaryFileUploadReqDTO;
import cn.edu.sdu.qd.oj.dto.FileDTO;
import cn.edu.sdu.qd.oj.dto.FileDownloadReqDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

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
    List<FileDTO> uploadBinaryFiles(@RequestBody List<BinaryFileUploadReqDTO> reqDTOList);

    @PostMapping(value = "/zipDownload", headers = "content-type=application/json")
    Resource download(@RequestBody List<FileDownloadReqDTO> reqDTOList);

}