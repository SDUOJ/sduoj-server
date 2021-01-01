/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.submit.dto.EachCheckpointResult;
import java.util.List;

@org.mapstruct.MapperConfig
public interface BaseSubmissionConverter<S, T> extends BaseConverter<S, T> {

    default List<EachCheckpointResult> checkpointResultsTo(byte[] bytes) {
        return SubmissionConverterUtils.checkpointResultsTo(bytes);
    }

    default byte[] checkpointResultsFrom(List<EachCheckpointResult> checkpointResults) {
        return SubmissionConverterUtils.checkpointResultsFrom(checkpointResults);
    }
}