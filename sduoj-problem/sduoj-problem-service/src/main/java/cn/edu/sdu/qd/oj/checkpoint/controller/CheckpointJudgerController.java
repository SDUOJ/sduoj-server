/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.checkpoint.controller;

import cn.edu.sdu.qd.oj.checkpoint.service.CheckpointFileService;
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
    @PostMapping(value = "/download", produces = "application/zip")
    public void zipDownload(@RequestBody List<String> checkpointIds, HttpServletResponse response) throws IOException {
        checkpointFileService.downloadCheckpointFiles(checkpointIds, new ZipOutputStream(response.getOutputStream()));
        String zipFileName = "checkpoints"; // TODO: 下载文件名定义问题
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"");
    }
}