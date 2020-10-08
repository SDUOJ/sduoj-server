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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @ClassName BytesToCheckpointIdsSerializer
 * @Description 序列化器 - 二进制转检查点结果
 * @Author zhangt2333
 * @Date 2020/5/5 19:07
 * @Version V1.0
 **/

public class BytesToCheckpointResultsSerializer extends JsonSerializer<byte[]> {

    @Override
    public void serialize(byte[] bytes, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        int size = bytes.length;
        if (size % 12 != 0) {
            jsonGenerator.writeString("The binary of checkpoint results is error!");
            return;
        }
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        jsonGenerator.writeStartArray();
        for (int i = 0; i < size; i += 12) {
            jsonGenerator.writeStartArray();
            jsonGenerator.writeNumber(wrap.getInt(i));
            jsonGenerator.writeNumber(wrap.getInt(i + 4));
            jsonGenerator.writeNumber(wrap.getInt(i + 8));
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndArray();
    }
}