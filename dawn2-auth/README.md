# dawn2-auth

该模块为认证中心模块,主要实现统一身份认证以及单点登录,会管理用户的基本信息

> 在springboot3=的oauth2服务中移除了很多以前的方法及认证模式(比如password签发token),网上很多实现都不可用了
>
> 具体实现可以参考官方文档
>

官方文档:[实现自定义grant_type:Implement an Extension Authorization Grant Type](https://docs.spring.io/spring-authorization-server/reference/guides/how-to-ext-grant-type.html)

1. OAuth2服务器的默认登陆由`UsernamePasswordAuthenticationFilter(拦截得/login)`
2. 拦截然后构建一个未经认证的`UsernamePasswordAuthenticationToken`然后交给`AuthenticationManager`进行认证
3. 然后manager会调用`AuthenticationProvider`的`supports`
   方法找到对应的token认证提供者最终会找到`AbstractUserDetailsAuthenticationProvider`的实现类`DaoAuthenticationProvider`
4. 进行用户认证,如果要自定义grant_type流程与上面一致,只是由`OAuth2TokenEndpointFilter`构建AuthenticationToken

## 重要

spring框架自带的认证请求,请求参数基本都是通过如下方式获取
> 由于获取请求参数,直接调用的HttpServletRequest.getParameterValues方法,该方法只能获取到url路径上以及form-data的请求类型参数.
>
> 现代框架一般的数据传输都是使用的是json通过httpBody传输,则框架根本获取不到请求参数,只能通过篡改请求实现
>

```
//框架自带的过滤器实现,框架自带的filter基本都是这种实现模式
//以下摘抄自 OAuth2TokenEndpointFilter
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

    //请求匹配
    //如果不匹配则不处理 流转到下一个过滤器
    if (!this.tokenEndpointMatcher.matches(request)) { 
        filterChain.doFilter(request, response);
        return;
    }

    try {
        //获取请求参数,直接调用的HttpServletRequest.getParameterValues方法,该方法只能获取到url路径上以及form-data的请求类型参数
        String[] grantTypes = request.getParameterValues(OAuth2ParameterNames.GRANT_TYPE);
        if (grantTypes == null || grantTypes.length != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.GRANT_TYPE);
        }

        Authentication authorizationGrantAuthentication = this.authenticationConverter.convert(request);
        if (authorizationGrantAuthentication == null) {
            throwError(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE, OAuth2ParameterNames.GRANT_TYPE);
        }
        if (authorizationGrantAuthentication instanceof AbstractAuthenticationToken) {
            ((AbstractAuthenticationToken) authorizationGrantAuthentication)
                    .setDetails(this.authenticationDetailsSource.buildDetails(request));
        }

        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication =
                (OAuth2AccessTokenAuthenticationToken) this.authenticationManager.authenticate(authorizationGrantAuthentication);
        this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, accessTokenAuthentication);
    } catch (OAuth2AuthenticationException ex) {
        SecurityContextHolder.clearContext();
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(LogMessage.format("Token request failed: %s", ex.getError()), ex);
        }
        this.authenticationFailureHandler.onAuthenticationFailure(request, response, ex);
    }
}
```

## dawn-auth实现功能

- 自定义grant_type实现多种认证模式,可自行拓展
- 手机验证码获取token
- 支持json请求获取token

## 关键类说明

### springOauth2框架自带类

- `OAuth2EndpointUtils`:框架自带的工具类
- `authorizationServerSecurityFilterChain`:服务器端点(就是过滤链的意思,通过过滤url路径实现的业务)
- `OAuth2TokenEndpointFilter`:框架自带的token下发实现,默认过滤路径`/oauth2/token`
- `UsernamePasswordAuthenticationFilter`:框架自带的登陆流程实现,默认路径`/login`
- `OAuth2AuthorizationCodeAuthenticationProvider`:框架自带的`authorization_code`授权码模式

### dawn实现类

> 自定义grant_type参照的是框架自带的`OAuth2AuthorizationCodeAuthenticationProvider`实现的authorization_code,
> 具体逻辑可以参考源码
>

- `filter/DawnTokenParamFilter`: 拦截token请求的url路径,通过包装`HttpServletRequest`
  请求实现接收json请求参数,属于篡改了原始请求提供给spring框架授权
- `config/DawnSecurityFilterSetting`:
  主要用来在spring框架自带的filter注册后,插入自己得一些filter实现,比如:`DawnTokenParamFilter`
- `config/DawnTokenEndpointSetting`: 自定义grant_type实现配置类,将业务实现类与需要的依赖bean加载到框架中
- `granter/AbstractDawnAuthenticationProvider`: 自定义授权提供者,处理对应的`AuthenticationToken`进行认证授权
- `granter/AbstractDawnGrantAuthenticationConverter`: 自定义授权token转换器,做一些参数认证及`grant_type`
  验证,将请求转换为需要的`AuthenticationToken`
- `granter/DawnPasswordAuthenticationProvider`:
  如果将这个bean加载到框架中则可以拦截到框架自带的`DaoAuthenticationProvider`认证流程,实现自己得登陆校验及登陆处理
- `granter/DawnAuthenticationToken`: 自定义得认证令牌,`AuthenticationConverter`
  验证后需要转换为对应的`AuthenticationToken`提交给下阶段的`AuthenticationProvider`处理
