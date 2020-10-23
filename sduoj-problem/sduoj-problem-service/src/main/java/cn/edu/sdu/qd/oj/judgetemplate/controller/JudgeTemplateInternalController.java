/*
 * Copyright 2020-2020 the original author or authors.
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
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JudgeTemplateInternalController implements JudgeTemplateApi {

    @Override
    public JudgeTemplateDTO query(Long id) {
        // TODO
        return null;
    }

}
