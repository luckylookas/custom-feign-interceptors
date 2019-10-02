package com.luckylukas.feign;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.luckylukas.feign.clients.ribbon.TestClientSingleInterceptorRibbon;
import com.luckylukas.feign.interceptors.FirstInterceptor;
import com.luckylukas.feign.interceptors.SecondInterceptor;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import feign.Client;
import feign.Feign;
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
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        "com.feign.base-package=com.luckylukas.feign.clients.ribbon",
        })
@EnableAutoConfiguration
@ContextConfiguration(classes = {
        RibbonAutoConfiguration.class,
        FeignRibbonClientAutoConfiguration.class,
        FeignClientRegistrarAutoConfiguration.class,
        FeignClientRegistrarAutoConfigurationRibbonIT.RibbonFeignTestConfiguration.class,
})
public class FeignClientRegistrarAutoConfigurationRibbonIT {

    @ClassRule
    public static WireMockClassRule wiremockRule = new WireMockClassRule(WireMockSpring.options().dynamicPort());

    @MockBean
    FirstInterceptor firstInterceptor;
    @MockBean
    SecondInterceptor secondInterceptor;

    @Autowired
    TestClientSingleInterceptorRibbon testClientSingleInterceptorRibbon;

    @Before
    public void init() {
        wiremockRule.stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));
    }

    @Test
    public void testCall_singleInterceptor_interceptorIsCalled() {
        testClientSingleInterceptorRibbon.testCall();
        verifyZeroInteractions(secondInterceptor);
        verify(firstInterceptor).apply(any());
    }

    @Ignore("test configuration")
    @Configuration
    public static class RibbonFeignTestConfiguration {

        @Bean
        public ServerList<Server> ribbonServerList() {
            return new StaticServerList<>(new Server("localhost", wiremockRule.port()));
        }

        @Bean
        @Primary
        Feign.Builder getLoadBalancingFeignBuilder(SpringClientFactory springClientFactory,
                                                   CachingSpringLoadBalancerFactory loadBalancerFactory) {
            return Feign.builder()
                .client(new LoadBalancerFeignClient(
                    new Client.Default(null, null),
                    loadBalancerFactory,
                    springClientFactory
                    )
                );
        }
    }
}
