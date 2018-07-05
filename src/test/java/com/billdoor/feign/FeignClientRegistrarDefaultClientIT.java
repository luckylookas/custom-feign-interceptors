package com.billdoor.feign;

import com.billdoor.feign.clients.defaultClient.TestClientSingleInterceptorDefault;
import com.billdoor.feign.clients.ribbon.TestClientSingleInterceptorRibbon;
import com.billdoor.feign.interceptors.FirstInterceptor;
import com.billdoor.feign.interceptors.SecondInterceptor;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "com.feign.base-package=com.billdoor.feign.clients.defaultClient"
})
@EnableAutoConfiguration
@ContextConfiguration(classes = {
        FeignClientRegistrarDefaultClientIT.DefaultFeignTestConfiguration.class,
        FeignClientRegistrar.class
})
public class FeignClientRegistrarDefaultClientIT {

    @ClassRule
    public static WireMockClassRule WIREMOCK = new WireMockClassRule(WireMockSpring.options().port(9999));

    @MockBean
    FirstInterceptor firstInterceptor;
    @MockBean
    SecondInterceptor secondInterceptor;

    @Autowired
    TestClientSingleInterceptorDefault testClientSingleInterceptorDefault;

    @Before
    public void init() {
        WIREMOCK.stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));
    }

    @Test
    public void testCall_singleInterceptor_interceptorIsCalled() {
        testClientSingleInterceptorDefault.testCall();
        verifyZeroInteractions(secondInterceptor);
        verify(firstInterceptor).apply(any());
    }

    @Configuration
    public static class DefaultFeignTestConfiguration {
        @Bean
        public ServerList<Server> ribbonServerList() {
            return new StaticServerList<>(new Server("localhost", WIREMOCK.port()));
        }
    }
}
