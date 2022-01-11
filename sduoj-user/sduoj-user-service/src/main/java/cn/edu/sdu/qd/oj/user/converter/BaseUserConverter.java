/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.user.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.user.dto.UserFeatureDTO;

@org.mapstruct.MapperConfig
public interface BaseUserConverter<S, T> extends BaseConverter<S, T> {
    default UserFeatureDTO featuresTo(String str) {
        return UserConvertUtils.featuresTo(str);
    }
    default String featuresFrom(UserFeatureDTO featureDTO) {
        return UserConvertUtils.featuresFrom(featureDTO);
    }
}