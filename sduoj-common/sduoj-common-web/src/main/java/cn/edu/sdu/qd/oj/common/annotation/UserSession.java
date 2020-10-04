package cn.edu.sdu.qd.oj.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
* @Description Annotation which is used to get UserSessionDTO in controller method
* @author zhangt2333
* @see cn.edu.sdu.qd.oj.common.entity.UserSessionDTO
* @see cn.edu.sdu.qd.oj.common.config.UserSessionMethodArgumentResolver
* @see cn.edu.sdu.qd.oj.common.config.UserSessionMethodArgumentResolverConfig
**/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface UserSession {
    boolean nullable() default false;
}
