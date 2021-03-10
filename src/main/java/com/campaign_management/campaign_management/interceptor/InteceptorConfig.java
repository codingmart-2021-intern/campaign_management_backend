package com.campaign_management.campaign_management.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Component
public class InteceptorConfig extends WebMvcConfigurationSupport {

    @Autowired
    private Inteceptor bookInteceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bookInteceptor);
    }
}
