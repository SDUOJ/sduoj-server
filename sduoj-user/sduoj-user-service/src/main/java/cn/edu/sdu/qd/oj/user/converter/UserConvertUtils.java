/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConvertUtils;
import cn.edu.sdu.qd.oj.user.dto.UserFeatureDTO;
import com.alibaba.fastjson.JSON;

import java.util.Optional;

/**
 * DTO-DO 特殊转换方法的统一收口处
 * @author zhangt2333
 */
public class UserConvertUtils extends BaseConvertUtils {
    public static UserFeatureDTO featuresTo(String str) {
        return Optional.ofNullable(JSON.parseObject(str, UserFeatureDTO.class)).orElseGet(UserFeatureDTO::new);
    }
    public static String featuresFrom(UserFeatureDTO featureDTO) {
        return JSON.toJSONString(featureDTO);
    }
}