# dawn2-auth

该模块为认证中心模块,主要实现统一身份认证以及单点登录,会管理用户的基本信息

> 在springboot3=的oauth2服务中移除了很多以前的方法及认证模式,网上很多实现都不可用了
>
> 具体实现可以参考官方文档
>

官方文档:[实现自定义grant_type:Implement an Extension Authorization Grant Type](https://docs.spring.io/spring-authorization-server/reference/guides/how-to-ext-grant-type.html)

OAuth2服务器的默认登陆由`UsernamePasswordAuthenticationFilter`
拦截然后构建一个未经认证的`UsernamePasswordAuthenticationToken`然后交给`AuthenticationManager`进行认证
然后manager会调用`AuthenticationProvider`的`supports`
方法找到对应的token认证提供者最终会找到`AbstractUserDetailsAuthenticationProvider`的实现类`DaoAuthenticationProvider`
进行用户认证

关键类

```
OAuth2EndpointUtils 
OAuth2TokenEndpointFilter //token接口拦截 /oauth2/token
UsernamePasswordAuthenticationFilter //登录接口拦截 /login
```