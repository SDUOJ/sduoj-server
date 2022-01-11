/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.controller;

import cn.edu.sdu.qd.oj.checkpoint.service.CheckpointFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


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
}