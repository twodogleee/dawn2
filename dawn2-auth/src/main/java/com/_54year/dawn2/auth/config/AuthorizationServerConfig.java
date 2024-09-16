package com._54year.dawn2.auth.config;

import com._54year.dawn2.auth.constant.DawnAuthParamNames;
import com._54year.dawn2.auth.constant.DawnAuthUrl;
import com._54year.dawn2.auth.filter.DawnTokenParamFilter;
import com._54year.dawn2.auth.granter.PhoneAuthenticationConverter;
import com._54year.dawn2.auth.granter.PhoneAuthenticationProvider;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * 认证服务器配置
 *
 * @author Andersen
 */
@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {


    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";


    /**
     * oauth2服务器 拦截链 提供 /oauth2/token,/oauth2/authorize 等拦截形成一整套得oauth2认证服务
     * <p>
     * 在OAuth2中，这些端点用于处理用户授权、获取令牌等操作。
     *
     * @param http http
     * @return 过滤器链
     * @throws Exception
     */
    @Bean
    @Order(1) //最高优先级
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, ApplicationContext context) throws Exception {

        //这个方法会应用OAuth2授权服务器的默认安全配置。它会自动配置一些基本的安全设置，比如保护OAuth2端点、配置认证和授权机制等。
        //使用默认的拦截器 提供url拦截
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        //这个方法获取OAuth2授权服务器的配置器，允许你进一步自定义OAuth2授权服务器的配置。
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)                // 让认证服务器元数据中有自定义的认证方式
                .authorizationServerMetadataEndpoint(metadata ->
                        metadata.authorizationServerMetadataCustomizer(customizer -> {
                                    customizer.grantType(DawnAuthParamNames.GRANT_PHONE_CODE);
                                    customizer.tokenEndpoint("/oauth2/token");
                                    customizer.build();
                                }

                        )
                )
                // 这个方法启用OpenID Connect 1.0（OIDC）支持。OIDC是基于OAuth2的认证协议，提供了用户身份验证和用户信息交换的标准化方式。
                //Customizer.withDefaults() 表示使用默认的OIDC配置。
                //http://127.0.0.1:8080/.well-known/openid-configuration 获取配置的元信息
                .oidc(Customizer.withDefaults());
        // 设置自定义用户确认授权页
//                .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI));


        // 未通过身份验证时重定向到登录页面
        // 授权端点
        //在Spring Security中，http.exceptionHandling() 方法用于配置异常处理机制。
        // 异常处理机制主要用于处理在请求处理过程中发生的各种异常情况，
        // 例如认证失败、授权失败等。通过配置异常处理，你可以自定义这些异常的处理方式，比如返回特定的错误页面、JSON响应等。
        http.exceptionHandling((exceptions) -> exceptions
                        //defaultAuthenticationEntryPointFor 方法用于为特定的请求路径或请求匹配器设置默认的认证入口点（Authentication Entry Point）。
                        //认证入口点是当用户未认证时，Spring Security用来处理未认证请求的组件。
                        .defaultAuthenticationEntryPointFor(
                                //entryPoint：要设置的默认认证入口点。 则登录入口等
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                //preferredMatcher：一个请求匹配器，用于指定哪些请求路径或请求应该使用这个默认认证入口点。
                                //这个地方是类型请求匹配器 如果是html请求 则重定向到login
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )

                );
//        return http.build();
        return this.customSettings(http, context);
    }

    /**
     * 自定义设置
     *
     * @param http http安全配置
     * @return 过滤器链
     * @throws Exception 异常信息
     */
    public SecurityFilterChain customSettings(HttpSecurity http, ApplicationContext context) throws Exception {
        DawnTokenEndpointSetting dawnTokenEndpointSetting = DawnTokenEndpointSetting.init(http, context);
        DefaultSecurityFilterChain filterChain = dawnTokenEndpointSetting
                .addTokenRequestConverter(new PhoneAuthenticationConverter())
                .addAuthenticationProvider(new PhoneAuthenticationProvider())
                .buildAndInitFilterSetting()
                .addFilterBefore(new DawnTokenParamFilter(), OAuth2TokenEndpointFilter.class)
                .getFilterChain();
        return filterChain;

    }


    /**
     * 配置密码解析器，使用BCrypt的方式对密码进行加密和验证
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置客户端注册Repository
     *
     * @param jdbcTemplate db数据源信息
     * @return 基于数据库的repository
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        // 基于db存储客户端，还有一个基于内存的实现 InMemoryRegisteredClientRepository
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }


    /**
     * 配置基于db得oAuth2得授权管理服务
     *
     * @param jdbcTemplate               db数据源信息
     * @param registeredClientRepository 配置客户端注册Repository
     * @return OAuth2AuthorizationService
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        //基于db的oauth2认证服务，还有一个基于内存的服务实现InMemoryOAuth2AuthorizationService
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);

    }

    /**
     * 配置基于db的授权确认管理服务
     * 当用客户端开启需要用户授权时 会跳转到中间页面  然后会把用户的确认信息保存到数据库
     *
     * @param jdbcTemplate               db数据源信息
     * @param registeredClientRepository 客户端repository
     * @return JdbcOAuth2AuthorizationConsentService
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        // 基于db的授权确认管理服务，还有一个基于内存的服务实现InMemoryOAuth2AuthorizationConsentService
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }


    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }


    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * 添加认证服务器配置，设置jwt签发者、默认端点请求地址等
     * 配置端点
     * {@link AuthorizationServerSettings.Builder#builder()}
     *
     * @return 认证服务器设置
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings
                .builder()
                //自定义token验证路径
                .tokenEndpoint(DawnAuthUrl.TOKEN_URL)
                .build();
    }

    /**
     * 创建自定义授权类型
     *
     * @return 授权类型检测
     */
//    public ConcurrentHashMap<String, DawnGrantTypeAuth> createCheck() {
//        ConcurrentHashMap<String, DawnGrantTypeAuth> checkMap = new ConcurrentHashMap<>();
//        checkMap.put(DawnAuthParamNames.GRANT_PHONE_CODE, new DawnPhoneAuth());
//        return checkMap;
//    }

}