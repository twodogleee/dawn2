package com._54year.dawn2.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DawnSecurityConfig {
    /**
     * 安全管理拦截器 再oauth2服务后面执行
     *
     * @param http http请求
     * @return 安全认证拦截器链
     * @throws Exception 异常信息
     */
    @Bean
    @Order(3) //第二优先级 然后形成拦截器链
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers(
                                        new AntPathRequestMatcher("/oauth2/**"),
                                        new AntPathRequestMatcher("/**/*.json"),
                                        new AntPathRequestMatcher("/**/*.html"),
                                        new AntPathRequestMatcher("/login")
                                )
                                .permitAll()
                                //其他全部需要认证
                                .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults())
                .csrf((csrf) -> csrf.disable())
                .oauth2ResourceServer((resourceServer) ->
                        resourceServer.jwt(Customizer.withDefaults()))
                .formLogin((formLogin) ->
                        formLogin.loginPage("/login")
                                .permitAll()
                );
//        http.addFilterBefore(new DawnAuthParamFilter(), OAuth2TokenEndpointFilter.class);
//                .httpBasic(Customizer.withDefaults())
//				// Form login handles the redirect to the login page from the
//				// authorization server filter chain
//                .formLogin(Customizer.withDefaults());


        return http.build();
    }


}
