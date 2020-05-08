/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @ClassName LongToHexStringSerializer
 * @Description 序列化器 - JavaScript 的 long 最长 53 bit，序列化成16进制字符串
 * @Author zhangt2333
 * @Date 2020/5/8 19:07
 * @Version V1.0
 **/

public class LongToHexStringSerializer extends JsonSerializer<Long> {
    @Override
    public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(Long.toHexString(aLong));
    }
}