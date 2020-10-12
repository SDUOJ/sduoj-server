/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.util;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import cn.edu.sdu.qd.oj.common.util.AssertUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CheckpointResultsToByteDeserializer extends JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        AssertUtils.isTrue(JsonToken.START_ARRAY.equals(p.getCurrentToken()), ApiExceptionEnum.PARAMETER_ERROR);
        List<Integer> list = new ArrayList<>();
        JsonToken jsonToken;
        while((jsonToken = p.nextToken()) != null) {
            if (JsonToken.START_ARRAY.equals(jsonToken)) {
                while((jsonToken = p.nextToken()) != null) {
                    if (JsonToken.VALUE_NUMBER_INT.equals(jsonToken)) {
                        list.add(Integer.parseInt(p.getText()));
                    } else if (JsonToken.END_ARRAY.equals(jsonToken)){
                        break;
                    }
                }
            } else if (JsonToken.END_ARRAY.equals(jsonToken)){
                break;
            }
        }
        ByteBuf byteBuf = Unpooled.buffer(list.size() * 4);
        list.forEach(byteBuf::writeInt);
        return byteBuf.array();
    }
}