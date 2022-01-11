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

import cn.edu.sdu.qd.oj.checkpoint.api.CheckpointApi;
import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointManageListDTO;
import cn.edu.sdu.qd.oj.checkpoint.service.CheckpointManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class CheckpointInternalController implements CheckpointApi {

    @Autowired
    private CheckpointManageService checkpointManageService;

    @Override
    public List<CheckpointManageListDTO> queryCheckpointListByProblemId(Long problemId) {
        return checkpointManageService.getCheckpoints(problemId);
    }
}