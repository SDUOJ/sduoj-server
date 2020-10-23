/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.problem.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConvertUtils;
import cn.edu.sdu.qd.oj.common.converter.BaseConverter;
import cn.edu.sdu.qd.oj.common.util.SpringContextUtils;
import cn.edu.sdu.qd.oj.judgetemplate.dto.JudgeTemplateListDTO;
import cn.edu.sdu.qd.oj.judgetemplate.service.JudgeTemplateService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.assertj.core.util.Lists;
import org.checkerframework.checker.units.qual.K;
import org.springframework.util.CollectionUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@org.mapstruct.MapperConfig
public interface BaseProblemConverter<S, T> extends BaseConverter<S, T> {

    default List<String> checkpointsTo(byte[] bytes) {
        int size = bytes != null ? bytes.length : 0;
        if (size == 0 || size % 8 != 0) {
            return Lists.newArrayList();
        }
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        List<String> checkpoints = new ArrayList<>(size / 8);
        for (int i = 0; i < size; i += 8) {
            checkpoints.add(Long.toHexString(wrap.getLong(i)));
        }
        return checkpoints;
    }

    default byte[] checkpointsFrom(List<String> checkpointHexList) {
        // 不能返回 new byte[0] 不然本来不更新 checkpoints 的，变成清空了
        if (checkpointHexList == null) {
            return null;
        }
        List<Long> checkpoints = checkpointHexList.stream().map(hex -> Long.valueOf(hex, 16)).collect(Collectors.toList());
        ByteBuf byteBuf = Unpooled.buffer(checkpoints.size() * 8);
        checkpoints.forEach(byteBuf::writeLong);
        return byteBuf.array();
    }

    default List<JudgeTemplateListDTO> judgeTemplatesTo(String judgeTemplates) {
        List<String> judgeTemplateIdStrList = BaseConvertUtils.stringToList(judgeTemplates);
        if (judgeTemplateIdStrList == null) {
            return new ArrayList<>();
        }
        List<Long> judgeTemplateIdList = judgeTemplateIdStrList.stream().map(Long::parseLong).collect(Collectors.toList());
        JudgeTemplateService judgeTemplateService = SpringContextUtils.getBean(JudgeTemplateService.class);
        return judgeTemplateService.listByIds(judgeTemplateIdList);
    }

    default String judgeTemplatesFrom(List<JudgeTemplateListDTO> judgeTemplates) {
        if (CollectionUtils.isEmpty(judgeTemplates)) {
            return null;
        }
        return BaseConvertUtils.listToString(judgeTemplates.stream().map(JudgeTemplateListDTO::getId).map(String::valueOf).collect(Collectors.toList()));
    }

    default List<Long> judgeTemplateIdsTo(String judgeTemplates) {
        List<String> judgeTemplateIdStrList = BaseConvertUtils.stringToList(judgeTemplates);
        if (judgeTemplateIdStrList == null) {
            return null;
        }
        return judgeTemplateIdStrList.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    default String judgeTemplateIdsFrom(List<Long> judgeTemplates) {
        if (judgeTemplates == null) {
            return null;
        }
        return BaseConvertUtils.listToString(judgeTemplates.stream().map(Object::toString).collect(Collectors.toList()));
    }
}