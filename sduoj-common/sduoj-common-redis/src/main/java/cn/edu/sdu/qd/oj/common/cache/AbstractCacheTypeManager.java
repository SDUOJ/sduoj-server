/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.cache;

import lombok.AllArgsConstructor;

import java.util.List;

/**
* @Description 缓存类型管理器基类，各个子域需要实现自己的缓存类型管理器，用 @component 标注子类，并在构造方法中对 cacheTypeList 赋值。
* @see CommonCacheTypeManager
**/
public abstract class AbstractCacheTypeManager {

    protected List<CacheType> cacheTypeList;

    public List<CacheType> getCacheTypeList() {
        return cacheTypeList;
    }

    @AllArgsConstructor
    public static class CacheType {
        public String key;
        public final int ttl;
    }
}
