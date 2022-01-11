/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.judgetemplate.util;

import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateConfigDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author zhangt2333
 */
public class TemplateConfigDeserializer extends JsonDeserializer<JudgeTemplateConfigDTO.TemplateConfig> {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public JudgeTemplateConfigDTO.TemplateConfig deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return objectMapper.readValue(jsonParser.getText(), JudgeTemplateConfigDTO.TemplateConfig.class);
    }
}