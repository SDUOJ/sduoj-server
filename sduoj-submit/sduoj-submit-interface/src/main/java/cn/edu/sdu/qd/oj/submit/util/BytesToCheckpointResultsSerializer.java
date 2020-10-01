/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

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