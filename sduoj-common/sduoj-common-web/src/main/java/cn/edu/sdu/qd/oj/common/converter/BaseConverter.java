package cn.edu.sdu.qd.oj.common.converter;

import java.util.List;
import java.util.stream.Stream;

@org.mapstruct.MapperConfig
public interface BaseConverter<S, T> {

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
}