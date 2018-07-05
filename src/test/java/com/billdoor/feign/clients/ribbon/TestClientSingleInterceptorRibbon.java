package com.billdoor.feign.clients.ribbon;

import com.billdoor.feign.InterceptedFeignClient;
import com.billdoor.feign.interceptors.FirstInterceptor;
import org.springframework.web.bind.annotation.GetMapping;

@InterceptedFeignClient(
        value = "http://lukas",
        interceptors = {FirstInterceptor.class}
        )
public interface TestClientSingleInterceptorRibbon {
    @GetMapping("/")
    String testCall();
}
