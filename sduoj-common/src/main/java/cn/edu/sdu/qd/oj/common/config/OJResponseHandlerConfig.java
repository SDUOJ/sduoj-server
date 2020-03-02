package cn.edu.sdu.qd.oj.common.config;

import cn.edu.sdu.qd.oj.common.entity.OJResponseBody;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Configuration
public class OJResponseHandlerConfig implements WebMvcConfigurer {
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(OJProcessor());
    }

    @Bean
    public ResponseResultProcessorDecorator OJProcessor() {
        return new ResponseResultProcessorDecorator();
    }



    public static class ResponseResultProcessorDecorator implements HandlerMethodReturnValueHandler, ApplicationContextAware {
        private RequestResponseBodyMethodProcessor delegate;
        private ApplicationContext applicationContext;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), OJResponseBody.class) ||
                    returnType.hasMethodAnnotation(OJResponseBody.class);
        }

        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException {
            if (delegate == null) {
                delegate = (RequestResponseBodyMethodProcessor)
                        Objects.requireNonNull(applicationContext.getBean(RequestMappingHandlerAdapter.class)
                                .getReturnValueHandlers())
                                .stream()
                                .filter(handler -> handler instanceof RequestResponseBodyMethodProcessor)
                                .findFirst()
                                .orElseThrow(RuntimeException::new);
            }
            delegate.handleReturnValue(ResponseResult.ok(returnValue), returnType, mavContainer, webRequest);
        }
    }
}
