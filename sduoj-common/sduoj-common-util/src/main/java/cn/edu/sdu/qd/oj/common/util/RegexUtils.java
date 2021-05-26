/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.common.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * cache some compiled regex for multiple usage
 * @author zhangt2333
 */
public class RegexUtils {

    private static final ConcurrentHashMap<String, Pattern> map = new ConcurrentHashMap<>();

    public static Pattern get(String regex) {
        return map.computeIfAbsent(regex, k -> Pattern.compile(k, Pattern.CASE_INSENSITIVE));
    }

    public static String regexFind(String text, String regex) {
        return regexFind(text, regex, 1);
    }

    public static String regexFind(String text, String regex, int index) {
        Matcher m = get(regex).matcher(text);
        return m.find() ? m.group(index) : "";
    }

    public static boolean matches(String text, String regex) {
        return get(regex).matcher(text).matches();
    }
}
