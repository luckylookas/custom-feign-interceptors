package com.luckylukas.feign.clients.ribbon;

import com.luckylukas.feign.InterceptedFeignClient;
import com.luckylukas.feign.interceptors.FirstInterceptor;
import org.springframework.web.bind.annotation.GetMapping;

@InterceptedFeignClient(
        value = "http://lukas",
        interceptors = {FirstInterceptor.class}
        )
public interface TestClientSingleInterceptorRibbon {
    @GetMapping("/")
    String testCall();
}
