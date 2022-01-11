/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.config;

import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.InternalApiException;
import com.alibaba.fastjson.JSONObject;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class FeignClientErrorDecoder implements feign.codec.ErrorDecoder {
 
    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(Util.toString(response.body().asReader()));
            return new InternalApiException(jsonObject.getIntValue("code"), jsonObject.getString("message"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new InternalApiException(ApiExceptionEnum.UNKNOWN_ERROR);
    }

}