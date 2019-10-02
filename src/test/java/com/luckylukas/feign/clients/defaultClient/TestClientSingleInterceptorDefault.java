package com.luckylukas.feign.clients.defaultClient;

import com.luckylukas.feign.InterceptedFeignClient;
import com.luckylukas.feign.interceptors.FirstInterceptor;
import org.springframework.web.bind.annotation.GetMapping;

@InterceptedFeignClient(
        value = "http://localhost:9999",
        interceptors = {FirstInterceptor.class}
        )
public interface TestClientSingleInterceptorDefault {
    @GetMapping("/")
    String testCall();
}
