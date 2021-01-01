/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.controller;

import cn.edu.sdu.qd.oj.judgetemplate.api.JudgeTemplateApi;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateDTO;
import cn.edu.sdu.qd.oj.judgetemplate.service.JudgeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JudgeTemplateInternalController implements JudgeTemplateApi {

    @Autowired
    private JudgeTemplateService judgeTemplateService;

    @Override
    public String idToTitle(Long id) {
        return judgeTemplateService.idToTitle(id);
    }

    @Override
    public Integer idToType(Long id) {
        return judgeTemplateService.idToType(id);
    }

    @Override
    public JudgeTemplateDTO query(Long id) {
        return judgeTemplateService.query(id);
    }
}
