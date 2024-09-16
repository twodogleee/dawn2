package com._54year.dawn2.auth.config;

import com._54year.dawn2.auth.granter.AbstractDawnAuthenticationProvider;
import com._54year.dawn2.auth.granter.AbstractDawnGrantAuthenticationConverter;
import com._54year.dawn2.auth.service.IDawnAuthCodeService;
import com._54year.dawn2.auth.service.IDawnUserInfoService;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.ArrayList;
import java.util.List;

public class DawnTokenEndpointSetting {
    /**
     * http安全配置
     */
    private HttpSecurity httpSecurity;
    /**
     * oAuth2服务配置类
     */
    private OAuth2AuthorizationServerConfigurer oAuth2AuthorizationServerConfigurer;

    /**
     * 授权提供者集合
     */
    private List<AbstractDawnAuthenticationProvider> authenticationProviders;
    /**
     * GRANT_TYPE 转换器集合
     */
    private List<AbstractDawnGrantAuthenticationConverter> authenticationConverters;

    private ApplicationContext context;

    private DawnTokenEndpointSetting() {

    }

    private DawnTokenEndpointSetting(HttpSecurity httpSecurity, ApplicationContext context) {
        this.httpSecurity = httpSecurity;
        this.oAuth2AuthorizationServerConfigurer = httpSecurity.getConfigurer(OAuth2AuthorizationServerConfigurer.class);
        this.authenticationProviders = new ArrayList<>();
        this.authenticationConverters = new ArrayList<>();
        this.context = context;

    }

    public static DawnTokenEndpointSetting init(HttpSecurity http, ApplicationContext context) {
        return new DawnTokenEndpointSetting(http, context);
    }

    public DefaultSecurityFilterChain build() throws Exception {
        //先将tokenEndpoint初始化到框架内
        this.oAuth2AuthorizationServerConfigurer.tokenEndpoint(
                (tokenEndpointConfigurer) -> {
                    for (AuthenticationProvider authenticationProvider : authenticationProviders) {
                        tokenEndpointConfigurer.authenticationProvider(authenticationProvider);
                    }
                    for (AuthenticationConverter authenticationConverter : authenticationConverters) {
                        tokenEndpointConfigurer.accessTokenRequestConverter(authenticationConverter);
                    }
                }
        );
        //build http配置 将tokenEndpoint加载到框架内 以及加载相关配置类
        // 初始化过滤器链
        DefaultSecurityFilterChain securityFilterChain = this.httpSecurity.build();
        //然后再从httpSecurity里面去获取这些类
        // 从框架中获取provider中所需的bean
//        AuthenticationManager authenticationManager = this.httpSecurity.getSharedObject(AuthenticationManager.class);

        OAuth2TokenGenerator<?> oAuth2TokenGenerator = this.httpSecurity.getSharedObject(OAuth2TokenGenerator.class);
        OAuth2AuthorizationService oAuth2AuthorizationService = this.httpSecurity.getSharedObject(OAuth2AuthorizationService.class);
        /*
         以上三个bean在build()方法之后调用是因为调用build方法时框架会尝试获取这些类，
         如果获取不到则初始化一个实例放入SharedObject中，所以要在build方法调用之后获取
         然后通过set方法设置进provider中，但是如果在build方法之后调用tokenEndpoint.authenticationProvider(provider)
         框架会提示unsupported_grant_type，因为已经初始化完了，在添加就不会生效了
         */

        //从上下文中获取相关bean
        IDawnUserInfoService dawnUserService = this.context.getBean(IDawnUserInfoService.class);
        IDawnAuthCodeService authCodeService = this.context.getBean(IDawnAuthCodeService.class);

        //调用set方法将关联类set进去
        for (AbstractDawnAuthenticationProvider provider : this.authenticationProviders) {
            provider.setTokenGenerator(oAuth2TokenGenerator);
            provider.setAuthorizationService(oAuth2AuthorizationService);
            provider.setDawnUserService(dawnUserService);
            provider.setAuthCodeService(authCodeService);
//            provider.setAuthenticationManager(authenticationManager);
        }
        return securityFilterChain;
    }

    /**
     * build过滤器链得同时创建filter设置
     *
     * @return 过滤器链设置
     * @throws Exception 异常信息
     */
    public DawnSecurityFilterSetting buildAndInitFilterSetting() throws Exception {
        DefaultSecurityFilterChain securityFilterChain = build();
        return DawnSecurityFilterSetting.init(securityFilterChain);
    }


    /**
     * 添加自定义token转换器
     *
     * @param converter 转换器
     * @return setting
     */
    public DawnTokenEndpointSetting addTokenRequestConverter(AbstractDawnGrantAuthenticationConverter converter) {
        this.authenticationConverters.add(converter);
        return this;
    }

    /**
     * 添加认证提供者
     *
     * @param provider 授权提供者
     * @return setting
     */
    public DawnTokenEndpointSetting addAuthenticationProvider(AbstractDawnAuthenticationProvider provider) {
        this.authenticationProviders.add(provider);
        return this;
    }


}
