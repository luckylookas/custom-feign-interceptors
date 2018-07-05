package com.billdoor.feign.interceptors;


import feign.RequestInterceptor;
import feign.RequestTemplate;


public class FirstInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("firstheader", "value");
    }
}
