/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.controller;

import cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint;
import cn.edu.sdu.qd.oj.checkpoint.service.CheckpointFileService;
import cn.edu.sdu.qd.oj.checkpoint.service.CheckpointManageService;
import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Map;

/**
 * @ClassName checkpointManageController
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/4/3 21:29
 * @Version V1.0
 **/

@Controller
@RequestMapping("/manage/checkpoint")
public class CheckpointManageController {

    @Autowired
    private CheckpointManageService checkpointManageService;

    @Autowired
    private CheckpointFileService checkpointFileService;

    /**
    * @Description 查看某个测试点详情
    * @param checkpointId
    * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint
    **/
    @PostMapping("/query")
    @ApiResponseBody
    public Checkpoint query(@RequestBody Map json) {
        return this.checkpointManageService.queryById((long) json.get("checkpointId"));
    }


    /**
    * @Description 上传单对文本文件作为测试点文件
    * @param input
    * @param output
    * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint
    **/
    @PostMapping(value = "/upload", headers = "content-type=application/json")
    @ApiResponseBody
    public Checkpoint upload(@RequestBody Map json) {
        String input = (String) json.get("input");
        String output = (String) json.get("output");
        if (StringUtils.isBlank(input) || StringUtils.isBlank(output)) {
            throw new ApiException(ApiExceptionEnum.CONTENT_IS_BLANK);
        }
        return checkpointFileService.updateCheckpointFile(input, output);
    }

    /**
    * @Description 批量上传成对的测试点文件，如果不配对或者写入到文件系统中出现错误，则全部回滚
    * @param files
    * @return cn.edu.sdu.qd.oj.checkpoint.pojo.Checkpoint[]
    **/
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    @ApiResponseBody
    public Checkpoint[] upload(@RequestParam("files") MultipartFile[] files) {
        return checkpointFileService.uploadCheckpointFiles(files);
    }
}