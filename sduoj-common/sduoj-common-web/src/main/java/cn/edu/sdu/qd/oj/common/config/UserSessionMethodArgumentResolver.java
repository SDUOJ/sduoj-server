package cn.edu.sdu.qd.oj.common.config;

import cn.edu.sdu.qd.oj.common.annotation.UserSession;
import cn.edu.sdu.qd.oj.common.entity.UserSessionDTO;
import cn.edu.sdu.qd.oj.common.enums.ApiExceptionEnum;
import cn.edu.sdu.qd.oj.common.exception.ApiException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class UserSessionMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(UserSession.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        UserSession parameterAnnotation = parameter.getParameterAnnotation(UserSession.class);
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final String userSessionDTOStr = request.getHeader("SDUOJUserInfo"); // TODO: 魔法值解决
        UserSessionDTO userSessionDTO = null;
        try {
            userSessionDTO =  JSON.parseObject(userSessionDTOStr, UserSessionDTO.class);
        } catch (Throwable ignore) {
        }
        if (!parameterAnnotation.nullable() && userSessionDTO == null) {
            log.info("未登录 {}", userSessionDTOStr);
            throw new ApiException(ApiExceptionEnum.USER_NOT_LOGIN);
        }
        return userSessionDTO;
    }
}
