package com.billdoor.feign.clients.defaultClient;

import com.billdoor.feign.InterceptedFeignClient;
import com.billdoor.feign.interceptors.FirstInterceptor;
import org.springframework.web.bind.annotation.GetMapping;

@InterceptedFeignClient(
        value = "http://localhost:9999",
        interceptors = {FirstInterceptor.class}
        )
public interface TestClientSingleInterceptorDefault {
    @GetMapping("/")
    String testCall();
}
