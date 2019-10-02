package com.luckylukas.feign;

import feign.RequestInterceptor;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@java.lang.annotation.Target(TYPE)
@Retention(RUNTIME)
public @interface InterceptedFeignClient {

    @AliasFor("target")
    String value() default "";

    @AliasFor("value")
    String target() default "";

    Class<? extends RequestInterceptor>[] interceptors() default {};

}
