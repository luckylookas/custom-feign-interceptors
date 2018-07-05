# custom-feign-interceptors
enables adding Interceptors for single Feign Clients, instead of injecting all available Interceptors into every client.


## usage

```java
@InterceptedFeignClient(
       value = "http://target-service,
       interceptors = {Interceptor.class, AnotherInterceptor.class}
) public interface feigClient {
}
```

Where value behaves analogous to the default `@FeignClient` (an uri or a service-name for eg- ribbon).
The interceptors are expected to be provided as Spring Beans and will be looked up for each client separately.

Can be used in combination with regular `@FeignClient` interfaces

### properties
| name | effect | default |
|------|:-------:|:-------:|
| com.feign.base-package | the package to be Scanned for `@InterceptedFeignClient` annotated classes | '*'

