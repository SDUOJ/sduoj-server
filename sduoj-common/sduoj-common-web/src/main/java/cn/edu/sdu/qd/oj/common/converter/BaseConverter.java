package cn.edu.sdu.qd.oj.common.converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.mapstruct.MapperConfig
public interface BaseConverter<S, T> {

    /** 基础 DO、DTO 转换方法 **/

    /**
     * 映射同名属性
     */
    T to(S source);

    /**
     * 反向，映射同名属性
     */
    @org.mapstruct.InheritInverseConfiguration(name = "to")
    S from(T target);

    /**
     * 映射同名属性，集合形式
     */
    @org.mapstruct.InheritConfiguration(name = "to")
    List<T> to(List<S> source);

    /**
     * 反向，映射同名属性，集合形式
     */
    @org.mapstruct.InheritConfiguration(name = "from")
    List<S> from(List<T> target);

    /**
     * 映射同名属性，集合流形式
     */
    List<T> to(Stream<S> sourceStream);

    /**
     * 反向，映射同名属性，集合流形式
     */
    List<S> from(Stream<T> targetStream);


    /** 常用 DO、DTO 转换方法 **/

    default List<String> stringToList(String str) {
        return StringUtils.isBlank(str) ? Lists.newArrayList() : Arrays.stream(str.split(",")).collect(Collectors.toList());
    }

    default String listToString(List<String> list) {
        return CollectionUtils.isEmpty(list) ? "" : StringUtils.join(list, ',');
    }

    default Map<String, String> stringToMap(String str) {
        return StringUtils.isBlank(str) ? Maps.newHashMap() : Arrays.stream(str.split(";")).collect(Collectors.toMap(s -> s.substring(0, s.indexOf(":")), s -> s.substring(s.indexOf(":") + 1), (k1, k2) -> k1));
    }

    default String mapToString(Map<String, String> map) {
        return CollectionUtils.isEmpty(map) ? "" : StringUtils.join(map.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.toList()), ";");
    }
}