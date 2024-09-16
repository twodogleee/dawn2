package com._54year.dawn2.auth.granter;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Collection;
import java.util.Map;

/**
 * 自定义认证token
 *
 * @author Andersen
 */
@Getter
public class DawnAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 基本的认证信息
     */
    private Authentication clientPrincipal;
    /**
     * 所附带得认证信息
     */
    private Map<String, String> additionalParameters;
    /**
     * 认证类型
     */
    private AuthorizationGrantType authorizationGrantType;


    public DawnAuthenticationToken(Authentication clientPrincipal, Map<String, String> additionalParameters, AuthorizationGrantType authorizationGrantType) {
        super(null);
        this.clientPrincipal = clientPrincipal;
        this.additionalParameters = additionalParameters;
        this.authorizationGrantType = authorizationGrantType;
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public DawnAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    /**
     * 获取认证参数
     *
     * @return 认证参数
     */
    @Override
    public Object getCredentials() {
        return additionalParameters;
    }

    /**
     * 获取基本的认证信息
     *
     * @return 这儿的认证信息直接是Client的认证信息
     */
    @Override
    public Object getPrincipal() {
        return clientPrincipal;
    }

}
