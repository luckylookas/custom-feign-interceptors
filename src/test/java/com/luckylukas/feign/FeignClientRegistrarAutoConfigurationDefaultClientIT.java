package com.luckylukas.feign;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.luckylukas.feign.clients.defaultClient.TestClientSingleInterceptorDefault;
import com.luckylukas.feign.interceptors.FirstInterceptor;
import com.luckylukas.feign.interceptors.SecondInterceptor;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
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
        "com.feign.base-package=com.luckylukas.feign.clients.defaultClient"
})
@EnableAutoConfiguration
@ContextConfiguration(classes = {
        FeignClientProperties.FeignClientConfiguration.class,
        FeignClientsConfiguration.class,
        FeignClientRegistrarAutoConfigurationDefaultClientIT.DefaultFeignTestConfiguration.class,
        FeignClientRegistrarAutoConfiguration.class
})
public class FeignClientRegistrarAutoConfigurationDefaultClientIT {

    @ClassRule
    public static WireMockClassRule wiremockRule = new WireMockClassRule(WireMockSpring.options().port(9999));

    @MockBean
    FirstInterceptor firstInterceptor;
    @MockBean
    SecondInterceptor secondInterceptor;

    @Autowired
    TestClientSingleInterceptorDefault testClientSingleInterceptorDefault;

    @Before
    public void init() {
        wiremockRule.stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));
    }

    @Test
    public void testCall_singleInterceptor_interceptorIsCalled() {
        testClientSingleInterceptorDefault.testCall();
        verifyZeroInteractions(secondInterceptor);
        verify(firstInterceptor).apply(any());
    }

    @Configuration
    @Ignore("test configuration")
    public static class DefaultFeignTestConfiguration {
        @Bean
        public ServerList<Server> ribbonServerList() {
            return new StaticServerList<>(new Server("localhost", wiremockRule.port()));
        }
    }
}
