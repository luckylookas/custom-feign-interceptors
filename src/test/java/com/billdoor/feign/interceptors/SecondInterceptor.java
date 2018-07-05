package com.billdoor.feign.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class SecondInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("secondheader", "value");
    }
}
