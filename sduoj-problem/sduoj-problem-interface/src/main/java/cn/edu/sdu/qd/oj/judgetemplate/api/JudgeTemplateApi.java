/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.api;

import cn.edu.sdu.qd.oj.common.util.RedisConstants;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/internal/judgetemplate")
public interface JudgeTemplateApi {
    String SERVICE_NAME = "problem-service";

    @GetMapping("/idToTitle")
    @Cacheable(key = "#id", value = RedisConstants.JUDGE_TEMPLATE_ID_TO_TITLE)
    String idToTitle(@RequestParam("id") Long id);

    @GetMapping("/idToType")
    @Cacheable(key = "#id", value = RedisConstants.JUDGE_TEMPLATE_ID_TO_TYPE)
    Integer idToType(@RequestParam("id") Long id);

    @GetMapping("/query")
    JudgeTemplateDTO query(@RequestParam("id") Long id);
}