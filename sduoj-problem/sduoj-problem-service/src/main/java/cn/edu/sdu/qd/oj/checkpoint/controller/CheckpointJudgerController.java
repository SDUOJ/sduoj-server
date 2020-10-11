/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.controller;

import cn.edu.sdu.qd.oj.checkpoint.service.CheckpointFileService;
import com.netflix.ribbon.proxy.annotation.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName CheckpointJudgerController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/11 18:37
 * @Version V1.0
 **/

@Slf4j
@Controller
@RequestMapping("/judger/checkpoint")
public class CheckpointJudgerController {

    @Autowired
    private CheckpointFileService checkpointFileService;

    /**
     * @Description 传入 checkpoint id 数组，以 zip 包形式下载数据
     * @param checkpointIds
     * @return void
     **/
    @PostMapping(value = "/download")
    public void zipDownload(@RequestBody List<String> checkpointIds,
                            HttpServletResponse response) throws IOException {
        log.warn("zipDownload: {}", checkpointIds);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment"); // 前端定义文件名
        response.setContentType("application/zip; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        checkpointFileService.downloadCheckpointFiles(checkpointIds, new ZipOutputStream(response.getOutputStream()));
    }
}