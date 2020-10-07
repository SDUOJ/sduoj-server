/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.controller;

import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointDTO;
import cn.edu.sdu.qd.oj.checkpoint.service.CheckpointFileService;
import cn.edu.sdu.qd.oj.checkpoint.service.CheckpointManageService;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName checkpointManageController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:29
 * @Version V1.0
 **/

@Controller
@RequestMapping("/manage/checkpoint")
@Slf4j
public class CheckpointManageController {

    @Autowired
    private CheckpointManageService checkpointManageService;

    @Autowired
    private CheckpointFileService checkpointFileService;

    /**
     * @param checkpointId
     * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint
     * @Description 查看某个测试点详情
     **/
    @GetMapping("/query")
    @ApiResponseBody
    public CheckpointDTO query(@RequestParam("checkpointId") String checkpointId) throws IOException {
        return this.checkpointFileService.queryCheckpointFileContent(checkpointId);
    }


    /**
     * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint
     * @Description 上传单对文本文件作为测试点文件
     **/
    @PostMapping(value = "/upload", headers = "content-type=application/json")
    @ApiResponseBody
    public CheckpointDTO upload(@RequestBody Map<String, String> json) {
        String input = json.get("input");
        String output = json.get("output");
        if (StringUtils.isBlank(input) || StringUtils.isBlank(output)) {
            throw new ApiException(ApiExceptionEnum.CONTENT_IS_BLANK);
        }
        return checkpointFileService.updateCheckpointFile(input, output);
    }

    /**
     * @param files
     * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint[]
     * @Description 批量上传成对的测试点文件，如果不配对或者写入到文件系统中出现错误，则全部回滚
     **/
    @PostMapping(value = "/uploadFiles", headers = "content-type=multipart/form-data")
    @ApiResponseBody
    public CheckpointDTO[] upload(@RequestParam("files") MultipartFile[] files) {
        return checkpointFileService.uploadCheckpointFiles(files);
    }

    /**
     * @Description 传入 checkpoint id 数组，以 zip 包形式下载数据
     * @param checkpointIds
     * @return void
     **/
    @PostMapping(value = "/download")
    public void zipDownload(@RequestBody List<String> checkpointIds,
                            HttpServletResponse response) throws IOException {
        log.warn("zipDownload: {}", checkpointIds);
        String zipFileName = "checkpoints.zip"; // TODO: 下载文件名定义问题
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        checkpointFileService.downloadCheckpointFiles(checkpointIds, new ZipOutputStream(response.getOutputStream()));
    }

    @GetMapping(value = "/list")
    @ApiResponseBody
    public List<CheckpointDTO> getCheckpoints(@RequestParam("problemCode") String problemCode) {
        return checkpointManageService.getCheckpoints(problemCode);
    }
}