/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @ClassName BytesToCheckpointIdsSerializer
 * @Description 序列化器 - 二进制转测试点id数组。不可用于多次序列化，即类不能在内部RPC中使用
 * @Author zhangt2333
 * @Date 2020/4/1 19:07
 * @Version V1.0
 **/

public class BytesToCheckpointIdsSerializer extends JsonSerializer<byte[]> {

    @Override
    public void serialize(byte[] bytes, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        int size = bytes.length;
        if (size % 8 != 0) {
            jsonGenerator.writeString("The binary of checkpointIds is error!");
            return;
        }
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        jsonGenerator.writeStartArray();
        for (int i = 0; i < size; i += 8) {
            jsonGenerator.writeString(Long.toHexString(wrap.getLong(i)));
        }
        jsonGenerator.writeEndArray();
    }
}