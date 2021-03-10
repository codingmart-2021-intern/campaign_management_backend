package com.campaign_management.campaign_management.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class Inteceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Inteceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        LOGGER.info("INTECEPTOR preHANDLE");

        boolean flag = true;
        String method = request.getMethod();
        if (method.equalsIgnoreCase("post") || method.equalsIgnoreCase("put")) {
            int contentLength = request.getContentLength();
            String contentType = request.getContentType();
            if (contentType != null && !contentType.equalsIgnoreCase("application/json")) {
                flag = false;
            }
            if (contentLength <= 2)
                flag = false;
        }
        if (!flag) {
            response.sendRedirect("/rest/api/v1/book/invalid");
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        LOGGER.info("INTECEPTOR postHANDLE");
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        LOGGER.info("INTECEPTOR afterHANDLE");
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
