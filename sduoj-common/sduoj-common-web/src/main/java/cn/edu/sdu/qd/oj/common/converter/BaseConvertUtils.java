/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description DTO-DO 特殊转换方法的统一收口处
 * @author zhangt2333
 **/
public class BaseConvertUtils {

    /** 常用 DO、DTO 转换方法 **/

    public static List<String> stringToList(String str) {
        return StringUtils.isBlank(str) ? null : Arrays.stream(str.split(",")).collect(Collectors.toList());
    }

    public static Set<String> stringToSet(String str) {
        return StringUtils.isBlank(str) ? null : Arrays.stream(str.split(",")).collect(Collectors.toSet());
    }

    public static String listToString(List<String> list) {
        return CollectionUtils.isEmpty(list) ? null : StringUtils.join(list, ',');
    }

    public static String setToString(Set<String> list) {
        return CollectionUtils.isEmpty(list) ? null : StringUtils.join(list, ',');
    }

    public static Map<String, String> stringToMap(String str) {
        return StringUtils.isBlank(str) ? null : Arrays.stream(str.split(";")).collect(Collectors.toMap(s -> s.substring(0, s.indexOf(":")), s -> s.substring(s.indexOf(":") + 1), (k1, k2) -> k1));
    }

    public static String mapToString(Map<String, String> map) {
        return CollectionUtils.isEmpty(map) ? null : StringUtils.join(map.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.toList()), ";");
    }
}