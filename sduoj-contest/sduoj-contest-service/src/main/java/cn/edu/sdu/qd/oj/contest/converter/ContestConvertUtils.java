/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.contest.converter;

import cn.edu.sdu.qd.oj.common.converter.BaseConvertUtils;
import cn.edu.sdu.qd.oj.common.util.SpringContextUtils;
import cn.edu.sdu.qd.oj.common.util.UserCacheUtils;
import cn.edu.sdu.qd.oj.contest.dto.ContestProblemListDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.util.CollectionUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description DTO-DO 特殊转换方法的统一收口处
 * @author zhangt2333
**/
public class ContestConvertUtils extends BaseConvertUtils {

    public static String userIdToUsername(Long userId) {
        return SpringContextUtils.getBean(UserCacheUtils.class).getUsername(userId);
    }

    public static List<ContestProblemListDTO> problemsTo(String problems) {
        if (StringUtils.isBlank(problems)) {
            return new ArrayList<>();
        }

        JSONArray json = JSON.parseArray(problems);
        List<ContestProblemListDTO> contestProblemListDTOList = new ArrayList<>(json.size());
        for (int i = 0, n = json.size(); i < n; i++) {
            JSONArray array = json.getJSONArray(i);
            contestProblemListDTOList.add(
                    ContestProblemListDTO.builder()
                            .problemCode(array.getString(0))
                            .problemTitle(array.getString(1))
                            .problemDescriptionId(array.getLong(2))
                            .problemWeight(array.getInteger(3))
                            .build()
            );
        }
        return contestProblemListDTOList;
    }

    public static String problemsFrom(List<ContestProblemListDTO> contestProblemListDTOList) {
        if (CollectionUtils.isEmpty(contestProblemListDTOList)) {
            return null;
        }

        JSONArray json = new JSONArray();
        contestProblemListDTOList.forEach(contestProblemListDTO -> {
            JSONArray array = new JSONArray();
            array.add(contestProblemListDTO.getProblemCode());
            array.add(contestProblemListDTO.getProblemTitle());
            array.add(contestProblemListDTO.getProblemDescriptionId());
            array.add(contestProblemListDTO.getProblemWeight());
            json.add(array);
        });
        return json.toJSONString();
    }


    public static List<String> participantsTo(byte[] participants) {
        List<Long> participantUserIdList = participantsToUserIdList(participants);
        UserCacheUtils userCacheUtils = SpringContextUtils.getBean(UserCacheUtils.class);
        return participantUserIdList.stream().map(userCacheUtils::getUsername).collect(Collectors.toList());
    }

    public static List<Long> participantsToUserIdList(byte[] participants) {
        if (participants == null) {
            return Lists.newArrayList();
        }
        ByteBuffer wrap = ByteBuffer.wrap(participants);
        List<Long> participantUserIdList = new ArrayList<>(participants.length / 8);
        for (int i = 0, n = participants.length; i < n; i += 8) {
            participantUserIdList.add(wrap.getLong(i));
        }
        return participantUserIdList;
    }


    public static byte[] participantsFrom(List<String> participantUsernameList) {
        UserCacheUtils userCacheUtils = SpringContextUtils.getBean(UserCacheUtils.class);
        List<Long> participantUserIdList = participantUsernameList.stream().map(userCacheUtils::getUserId).collect(Collectors.toList());
        return participantsFromUserIdList(participantUserIdList);
    }

    public static byte[] participantsFromUserIdList(List<Long> participantUserIdList) {
        ByteBuf byteBuf = Unpooled.buffer(participantUserIdList.size() * 8);
        participantUserIdList.forEach(byteBuf::writeLong);
        return byteBuf.array();
    }

}