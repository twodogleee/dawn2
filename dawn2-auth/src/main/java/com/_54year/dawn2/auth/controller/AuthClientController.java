package com._54year.dawn2.auth.controller;

import com._54year.dawn2.core.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 认证 客户端业务处理
 *
 * @author Andersen
 */
@RestController
public class AuthClientController {

    RegisteredClientRepository registeredClientRepository;
    PasswordEncoder passwordEncoder;

    public AuthClientController(RegisteredClientRepository registeredClientRepository, PasswordEncoder passwordEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
    }



    /**
     * 注册客户端
     *
     * @param registeredClient 客户端信息
     * @return 是否成功
     */
    @PostMapping("oauth2/createClient")
    public R<RegisteredClient> create(@RequestBody RegisteredClient registeredClient) {
        System.out.println(passwordEncoder.encode("123456"));
        registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                // 客户端id
                .clientId(registeredClient.getClientId())
                // 客户端秘钥，使用密码解析器加密
                .clientSecret(passwordEncoder.encode("123456"))
                // 客户端认证方式，基于请求头的认证
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // 配置资源服务器使用该客户端获取授权时支持的方式
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                // 授权码模式回调地址，oauth2.1已改为精准匹配，不能只设置域名，并且屏蔽了localhost，本机使用127.0.0.1访问
                // 这里每次启动是不会该数据库的，所以改了这个链接要相应的改一下数据库
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
                .redirectUri("https://www.baidu.com/")
                .redirectUri("http://127.0.0.1:8080/notify/oauth2/code")
                // 该客户端的授权范围，OPENID与PROFILE是IdToken的scope，获取授权时请求OPENID的scope时认证服务会返回IdToken
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                // 自定scope
                .scope("message.read")
                .scope("message.write")
                // 客户端设置，设置用户需要确认授权
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .build();
        registeredClientRepository.save(registeredClient);
        return R.ok(registeredClient);
    }
}
