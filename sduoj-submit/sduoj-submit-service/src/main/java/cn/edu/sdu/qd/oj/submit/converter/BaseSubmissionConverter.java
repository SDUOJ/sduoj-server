/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.submit.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.common.util.CollectionUtils;
import cn.edu.sdu.qd.oj.submit.dto.EachCheckpointResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.assertj.core.util.Lists;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@org.mapstruct.MapperConfig
public interface BaseSubmissionConverter<S, T> extends BaseConverter<S, T> {

    default List<EachCheckpointResult> checkpointResultsTo(byte[] bytes) {
        int size = bytes != null ? bytes.length : 0;
        if (size == 0 || size % (4*4) != 0) {
            return Lists.newArrayList();
        }
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        List<EachCheckpointResult> checkpointResults = new ArrayList<>(size / (4*4));
        for (int i = 0; i < size; i += 16) {
            checkpointResults.add(new EachCheckpointResult(
                    wrap.getInt(i), wrap.getInt(i+4), wrap.getInt(i+8), wrap.getInt(i+12))
            );
        }
        return checkpointResults;
    }

    default byte[] checkpointResultsFrom(List<EachCheckpointResult> checkpointResults) {
        if (CollectionUtils.isEmpty(checkpointResults) || checkpointResults.stream().anyMatch(o -> o.size() != 4)) {
            return null;
        }
        ByteBuf byteBuf = Unpooled.buffer(checkpointResults.size() * 16);
        checkpointResults.forEach(o -> o.forEach(byteBuf::writeInt));
        return byteBuf.array();
    }
}