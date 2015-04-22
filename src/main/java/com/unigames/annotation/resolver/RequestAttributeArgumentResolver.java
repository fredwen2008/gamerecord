package com.unigames.annotation.resolver;

import com.unigames.annotation.RequestAttribute;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

/**
 * Created by wenfeng on 14/12/16.
 */
public class RequestAttributeArgumentResolver implements WebArgumentResolver {

    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        Annotation[] paramAnns = methodParameter.getParameterAnnotations();
        for (Annotation paramAnn : paramAnns) {
            if (RequestAttribute.class.isInstance(paramAnn)) {
                RequestAttribute reqAttr = (RequestAttribute) paramAnn;
                HttpServletRequest httpRequest = (HttpServletRequest) webRequest.getNativeRequest();
                return httpRequest.getAttribute(reqAttr.value());
            }
        }
        return WebArgumentResolver.UNRESOLVED;
    }
}
