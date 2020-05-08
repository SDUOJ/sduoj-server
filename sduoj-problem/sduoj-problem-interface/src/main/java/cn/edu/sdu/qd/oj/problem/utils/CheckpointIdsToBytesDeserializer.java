/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.problem.utils;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
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

/**
 * @ClassName CheckpointIdsToBytesDeserializer
 * @Description 反序列化器，checkpointIds 数组转为 byte[]
 * @Author zhangt2333
 * @Date 2020/5/8 22:25
 * @Version V1.0
 **/

public class CheckpointIdsToBytesDeserializer extends JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        if (!JsonToken.START_ARRAY.equals(jsonParser.getCurrentToken()))
            throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
        List<Long> list = new ArrayList<>();
        JsonToken jsonToken = null;
        while((jsonToken = jsonParser.nextToken()) != null) {
            if (JsonToken.VALUE_STRING.equals(jsonToken))
                list.add(Long.valueOf(jsonParser.getText(), 16));
            else if (!JsonToken.END_ARRAY.equals(jsonToken) && !JsonToken.END_OBJECT.equals(jsonToken))
                throw new ApiException(ApiExceptionEnum.PARAMETER_ERROR);
        }
        ByteBuf byteBuf = Unpooled.buffer(list.size() * 8);
        list.forEach(byteBuf::writeLong);
        return byteBuf.array();
    }
}