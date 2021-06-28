/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.util;

import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateConfigDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author zhangt2333
 */
public class TemplateConfigSerializer extends JsonSerializer<JudgeTemplateConfigDTO.TemplateConfig> {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void serialize(JudgeTemplateConfigDTO.TemplateConfig value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(objectMapper.writeValueAsString(value));
    }
}