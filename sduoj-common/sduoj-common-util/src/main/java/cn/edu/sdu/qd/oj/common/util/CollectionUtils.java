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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CollectionUtils {

    public static <T> Map<T, Integer> getMapToIndex(Collection<T> collection) {
        Map<T, Integer> map = new HashMap<>(collection.size());
        int index = 0;
        for (T t : collection) {
            map.put(t, index++);
        }
        return map;
    }
}
