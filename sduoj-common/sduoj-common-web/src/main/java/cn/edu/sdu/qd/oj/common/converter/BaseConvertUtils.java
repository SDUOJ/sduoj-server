package cn.edu.sdu.qd.oj.common.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    public static String listToString(List<String> list) {
        return CollectionUtils.isEmpty(list) ? null : StringUtils.join(list, ',');
    }

    public static Map<String, String> stringToMap(String str) {
        return StringUtils.isBlank(str) ? null : Arrays.stream(str.split(";")).collect(Collectors.toMap(s -> s.substring(0, s.indexOf(":")), s -> s.substring(s.indexOf(":") + 1), (k1, k2) -> k1));
    }

    public static String mapToString(Map<String, String> map) {
        return CollectionUtils.isEmpty(map) ? null : StringUtils.join(map.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.toList()), ";");
    }
}