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

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CollectionUtils {

    /**
    * @Description 容器 -> 元素映射为下标的 map
    * @param collection
    * @return java.util.Map<T,java.lang.Integer>
    **/
    public static <T> Map<T, Integer> getMapToIndex(Collection<T> collection) {
        if (isEmpty(collection)) {
            return new HashMap<>();
        }
        Map<T, Integer> map = new HashMap<>(collection.size());
        int index = 0;
        for (T t : collection) {
            map.put(t, index++);
        }
        return map;
    }

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }
}
