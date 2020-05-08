/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.utils;

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