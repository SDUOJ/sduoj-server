/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.util;

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