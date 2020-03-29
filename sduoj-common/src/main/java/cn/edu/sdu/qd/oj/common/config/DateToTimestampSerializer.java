/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 * @ClassName TestSerializer
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/3/29 12:32
 * @Version V1.0
 **/

public class DateToTimestampSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (date != null) {
            // 避免多次序列化除以 1000，特判大于 1e12，即本代码所在系统时间必须处于 2001-09-09 09:46:40 至 33658-09-09 09:46:40
            long timestamp = date.getTime();
            jsonGenerator.writeObject(timestamp > 1e12 ? (int) (timestamp / 1000) : (int) timestamp);
        } else {
            jsonGenerator.writeObject(null);
        }
    }
}