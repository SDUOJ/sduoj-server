package cn.edu.sdu.qd.oj.common.entity;

import cn.edu.sdu.qd.oj.common.util.NoNullFieldStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 所有模型的基类
 */
public class BaseDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, new NoNullFieldStringStyle());
    }
}