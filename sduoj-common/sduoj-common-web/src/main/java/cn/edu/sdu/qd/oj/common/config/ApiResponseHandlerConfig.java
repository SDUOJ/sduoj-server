package cn.edu.sdu.qd.oj.common.config;

import cn.edu.sdu.qd.oj.common.entity.ApiResponseBody;
import cn.edu.sdu.qd.oj.common.entity.ResponseResult;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
public class ApiResponseHandlerConfig implements InitializingBean {

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = adapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList(returnValueHandlers);
        this.decorateHandlers(handlers);
        adapter.setReturnValueHandlers(handlers);
    }

    private void decorateHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        for (int i = 0; i < handlers.size(); i++) {
            HandlerMethodReturnValueHandler handlerMethodReturnValueHandler = handlers.get(i);
            if (handlerMethodReturnValueHandler instanceof ViewNameMethodReturnValueHandler) {
                // 在视图处理器前插入本返回值处理器，以免 String 类型返回值被提前按视图进行处理
                handlers.add(i, OJProcessor());
                return;
            }
        }
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
            return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ApiResponseBody.class) ||
                    returnType.hasMethodAnnotation(ApiResponseBody.class);
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