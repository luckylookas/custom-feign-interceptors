package com.billdoor.feign;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Configuration;

import feign.Contract;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.PostConstruct;
import java.beans.Introspector;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Import(FeignClientsConfiguration.class)
public class FeignClientRegistrar {

    private final ConfigurableBeanFactory beanFactory;
    private final Decoder decoder;
    private final Encoder encoder;
    private final Contract contract;

    @Setter
    @Value("${com.feign.base-package}")
    private String basePackage = "*";

    @PostConstruct
    public void registerFeignClients() {
        getPossFeignClientClasses().stream()
            .map(bean -> {
                try {
                    return Class.forName(bean.getBeanClassName());
                } catch (ClassNotFoundException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .forEach(clientClass-> {
                Feign.Builder builder = beanFactory.getBean(Feign.Builder.class);

                InterceptedFeignClient feignClient = clientClass.getAnnotation(InterceptedFeignClient.class);

                beanFactory.registerSingleton(
                    Introspector.decapitalize(clientClass.getSimpleName()),
                    builder
                        .decoder(decoder)
                        .encoder(encoder)
                        .contract(contract)
                        .requestInterceptors(findBeansForClasses(feignClient))
                        .target(clientClass ,feignClient.value()));
            });
    }

    private Set<RequestInterceptor> findBeansForClasses(InterceptedFeignClient annotated) {
        return Arrays.stream(annotated.interceptors())
                .distinct()
                .map(beanFactory::getBean)
                .collect(Collectors.toSet());
    }

    private Set<BeanDefinition> getPossFeignClientClasses() {
        ClassPathScanningCandidateComponentProvider provider = new AnnotatedInterfaceClassPathScanner();
        provider.addIncludeFilter(new AnnotationTypeFilter(InterceptedFeignClient.class));
        return provider.findCandidateComponents(basePackage);
    }

    private static class AnnotatedInterfaceClassPathScanner extends ClassPathScanningCandidateComponentProvider {
        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            return (metadata.isInterface());
        }
    }
}
