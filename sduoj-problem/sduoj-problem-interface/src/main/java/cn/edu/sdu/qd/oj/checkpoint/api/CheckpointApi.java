/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.api;

import cn.edu.sdu.qd.oj.checkpoint.dto.CheckpointManageListDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/internal/checkpoint")
public interface CheckpointApi {
    String SERVICE_NAME = "problem-service";

    @GetMapping(value = "/queryCheckpointListByProblemId")
    List<CheckpointManageListDTO> queryCheckpointListByProblemId(@RequestParam("problemId") Long problemId);
}
