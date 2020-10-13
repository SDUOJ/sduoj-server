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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.assertj.core.util.Lists;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@org.mapstruct.MapperConfig
public interface BaseSubmissionConverter<S, T> extends BaseConverter<S, T> {

    default List<List<Integer>> checkpointResultsTo(byte[] bytes) {
        int size = bytes.length;
        if (size % (3*4) != 0) {
            return null;
        }
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        List<List<Integer>> checkpointResults = new ArrayList<>(size / (3*4));
        for (int i = 0; i < size; i += 12) {
            checkpointResults.add(Lists.newArrayList(wrap.getInt(i), wrap.getInt(i+4), wrap.getInt(i+8)));
        }
        return checkpointResults;
    }

    default byte[] checkpointResultsFrom(List<List<Integer>> checkpointResults) {
        ByteBuf byteBuf = Unpooled.buffer(checkpointResults.size() * 12);
        if (checkpointResults.stream().anyMatch(o -> o.size() != 3)) {
            return null;
        }
        checkpointResults.forEach(o -> o.forEach(byteBuf::writeInt));
        return byteBuf.array();
    }
}