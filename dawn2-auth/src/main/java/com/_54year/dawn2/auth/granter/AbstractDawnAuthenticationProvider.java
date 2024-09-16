package com._54year.dawn2.auth.granter;

import com._54year.dawn2.auth.constant.DawnAuthParamNames;
import com._54year.dawn2.auth.entity.DawnUserInfo;
import com._54year.dawn2.auth.service.IDawnAuthCodeService;
import com._54year.dawn2.auth.service.IDawnUserInfoService;
import com._54year.dawn2.auth.util.DawnAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 自定义认证提供者
 * 整个调用链为 clientAuth -> AuthConverter -> AuthProvider 签发认证信息
 * 参照 {@link OAuth2AuthorizationCodeAuthenticationProvider}
 * 详情可以参考:https://docs.spring.io/spring-authorization-server/reference/guides/how-to-ext-grant-type.html
 */
@Slf4j
public abstract class AbstractDawnAuthenticationProvider implements AuthenticationProvider {


    /**
     * token 加密
     */
    private OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    /**
     * 框架自带的 OAuth2AuthorizationService
     */
    private OAuth2AuthorizationService authorizationService;

    /**
     * 用户信息查询
     */
    private IDawnUserInfoService dawnUserService;

    /**
     * 验证码业务处理
     */
    private IDawnAuthCodeService authCodeService;

    public AbstractDawnAuthenticationProvider() {

    }

    public AbstractDawnAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                              OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
    }

    /**
     * 认证方法 用来提供认证 颁发token
     * {@link OAuth2AuthorizationCodeAuthenticationProvider#authenticate(Authentication)}
     *
     * @param authentication the authentication request object.
     * @return 认证信息
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //获取自定义得认证信息
        DawnAuthenticationToken dawnAuthenticationToken = (DawnAuthenticationToken) authentication;
        //当前请求的授权类型
        AuthorizationGrantType authorizationGrantType = dawnAuthenticationToken.getAuthorizationGrantType();
        //获取客户端的认证信息 则基本掉用信息
        OAuth2ClientAuthenticationToken clientPrincipal = DawnAuthUtils.getAuthenticatedClientElseThrowInvalidClient(dawnAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        //如果客户端为空 或者 不包含授权类型 则结束
        if (registeredClient == null
                || !registeredClient.getAuthorizationGrantTypes().contains(authorizationGrantType)
        ) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
        if (log.isTraceEnabled()) {
            log.trace("Retrieved registered client");
        }
        //认证之后获取一个UserInfo
        //这是一个认证方法 认证之后并返回一个用户信息
        DawnUserInfo userInfo = loadUserInfo(dawnAuthenticationToken, dawnUserService, authCodeService);

        //授权信息
        UsernamePasswordAuthenticationToken userAuthorization = createSucAuthorization(authentication, userInfo);

        //授权范围
        Set<String> scopes = new HashSet<>();
        scopes.add(DawnAuthParamNames.DEF_SCOPE);

        /*
         * {@link OAuth2AuthorizationCodeAuthenticationProvider#authenticate(Authentication)}
         */
        // @formatter:off
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient) //客户端信息
                .principal(userAuthorization) //用户信息
                .authorizationServerContext(AuthorizationServerContextHolder.getContext()) //上下文
                .authorizedScopes(scopes) //dataScope 默认all
                .authorizationGrantType(authorizationGrantType) //获取授权类型
                .authorizationGrant(dawnAuthenticationToken);
        // @formatter:on

        // Initialize the OAuth2Authorization
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                //授权名称
                .principalName(userAuthorization.getName()) //授权得用户名称
//                .principalName(userInfo.getUserId().toString())
                .authorizationGrantType(authorizationGrantType) //授权类型
                .authorizedScopes(scopes) //授权范围
                .attribute(Principal.class.getName(), userAuthorization); //认证信息

        // ----- Access token -----
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.", "");
            throw new OAuth2AuthenticationException(error);
        }

        if (log.isTraceEnabled()) {
            log.trace("Generated access token");
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(accessToken, (metadata) ->
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        // ----- Refresh token -----
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (generatedRefreshToken != null) {
                if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                            "The token generator failed to generate a valid refresh token.", "");
                    throw new OAuth2AuthenticationException(error);
                }

                if (log.isTraceEnabled()) {
                    log.trace("Generated refresh token");
                }

                refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
                authorizationBuilder.refreshToken(refreshToken);
            }
        }

        // ----- ID token ----- 不颁发id Token

        // ----- 保存token -----
        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);
        if (log.isTraceEnabled()) {
            log.trace("Saved authorization");
        }
        Map<String, Object> additionalParameters = Collections.emptyMap();

        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
    }

    /**
     * 创建验证成功的用户认证信息
     *
     * @param authentication 认证信息
     * @param dawnUserInfo   用户信息
     * @return 认证信息
     */
    public UsernamePasswordAuthenticationToken createSucAuthorization(Authentication authentication, DawnUserInfo dawnUserInfo) {
        /*
            "java.security.Principal": {
        "@class": "org.springframework.security.authentication.UsernamePasswordAuthenticationToken",
        "authorities": [
            "java.util.Collections$UnmodifiableRandomAccessList",
            []
        ],
        "details": {
            "@class": "org.springframework.security.web.authentication.WebAuthenticationDetails",
            "remoteAddress": "0:0:0:0:0:0:0:1",
            "sessionId": "14FA5A74A8E3B5613A9C7C6F9163B763"
        },
        "authenticated": true,
        "principal": {
            "@class": "com._54year.dawn2.auth.entity.DawnUserInfo",
            "tenantId": null,
            "userId": 123,
            "username": "test",
            "email": null,
            "phone": null,
            "password": null,
            "authorities": null,
            "accountNonExpired": true,
            "accountNonLocked": true,
            "credentialsNonExpired": true,
            "enabled": true
        },
        "credentials": null
    }
         */
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(dawnUserInfo, null, dawnUserInfo.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(authentication.getDetails());
        return usernamePasswordAuthenticationToken;
    }

    /**
     * 检查并获取用户信息
     *
     * @param dawnAuthenticationToken 授权请求信息
     * @return 用户信息
     */
    public abstract DawnUserInfo loadUserInfo(DawnAuthenticationToken dawnAuthenticationToken,
                                              IDawnUserInfoService dawnUserService,
                                              IDawnAuthCodeService authCodeService);

//    private DawnUserInfo getUserInfo() {
//        DawnUserInfo dawnUserInfo = new DawnUserInfo();
//        dawnUserInfo.setUserId(123456L);
//        dawnUserInfo.setUsername("test");
//        dawnUserInfo.setEmail("test@test.com");
//        return dawnUserInfo;
//    }


    /**
     * 是否需要处理的token
     *
     * @param authentication 认证信息
     * @return ture 提供认证 false 不满足
     */
    @Override
    public boolean supports(Class<?> authentication) {
        //提交上来的AuthToken 为自定义得token才处理
        return DawnAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * token生成器
     *
     * @param tokenGenerator token生成器
     */
    public void setTokenGenerator(OAuth2TokenGenerator<?> tokenGenerator) {
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.tokenGenerator = tokenGenerator;
    }

//    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
//        Assert.notNull(authorizationService, "authenticationManager cannot be null");
//        this.authenticationManager = authenticationManager;
//    }

    /**
     * OAuth2AuthorizationService
     *
     * @param authorizationService 认证信息存储
     */
    public void setAuthorizationService(OAuth2AuthorizationService authorizationService) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        this.authorizationService = authorizationService;
    }

    /**
     * 用户信息业务处理
     *
     * @param dawnUserService 用户信息
     */
    public void setDawnUserService(IDawnUserInfoService dawnUserService) {
        Assert.notNull(dawnUserService, "dawnUserService cannot be null");
        this.dawnUserService = dawnUserService;
    }

    /**
     * 认证码业务处理
     *
     * @param authCodeService 认证码业务处理
     */
    public void setAuthCodeService(IDawnAuthCodeService authCodeService) {
        Assert.notNull(authCodeService, "authCodeService cannot be null");
        this.authCodeService = authCodeService;
    }


}
