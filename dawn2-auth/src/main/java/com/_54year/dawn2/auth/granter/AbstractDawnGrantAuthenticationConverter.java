package com._54year.dawn2.auth.granter;

import com._54year.dawn2.auth.constant.DawnAuthMsg;
import com._54year.dawn2.auth.util.DawnAuthUtils;
import com._54year.dawn2.core.exception.DawnAuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义grant_type处理 认证信息转换
 * 当拦截到对应的grant_type时 将请求转换为Authentication身份认证信息
 * 整个调用链为 clientAuth -> AuthConverter -> AuthProvider 签发认证信息
 * 详情可以参考:https://docs.spring.io/spring-authorization-server/reference/guides/how-to-ext-grant-type.html
 *
 * @author Andersen
 */
public abstract class AbstractDawnGrantAuthenticationConverter implements AuthenticationConverter {


    /**
     * request转Authentication
     *
     * @param request 请求
     * @return 框架用的认证信息
     */
    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {
        //获取grant_type
        // grant_type (REQUIRED)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        //如果不为需要解析grant_type则结束
        if (!grantTypeCheck(grantType)) {
            return null;
        }
        //这是客户端的认证信息 则token接口提供的基本认证信息
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        //从请求中获取form表单参数
        Map<String, String> parameters = DawnAuthUtils.getParameters(request);
        if (ObjectUtils.isEmpty(clientPrincipal) || ObjectUtils.isEmpty(parameters)) {
            throw new DawnAuthException(DawnAuthMsg.PARAM_EMPTY);
        }

        // code (REQUIRED)
//        if (dawnGrantTypeAuth.grantTypeParamCheck(parameters)) {
//            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
//        }

        //请求参数校验
        additionalParametersCheck(parameters);

        // 认证参数 用户名/密码/等等等
        Map<String, String> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
                    !key.equals(OAuth2ParameterNames.CLIENT_ID) &&
                    !key.equals(OAuth2ParameterNames.CODE)) {
                additionalParameters.put(key, value);
            }
        });
        return unauthenticated(clientPrincipal, additionalParameters, grantType);
    }

    /**
     * 授权类型判断
     *
     * @param grantType 授权类型
     * @return ture可处理 false 不可处理
     */
    public abstract boolean grantTypeCheck(String grantType);

    /**
     * 请求参数校验
     *
     * @param parameters 请求参数
     */
    public abstract void additionalParametersCheck(Map<String, String> parameters);

    /**
     * 创建一个未认证的认证信息
     *
     * @param clientPrincipal      客户端的认证信息
     * @param additionalParameters 用来认证的信息
     * @return token
     */
    public DawnAuthenticationToken unauthenticated(Authentication clientPrincipal, Map<String, String> additionalParameters, String grantType) {
        AuthorizationGrantType authorizationGrantType = new AuthorizationGrantType(grantType);
        DawnAuthenticationToken dawnAuthenticationToken = new DawnAuthenticationToken(clientPrincipal, additionalParameters, authorizationGrantType);
        dawnAuthenticationToken.setAuthenticated(false);
        return dawnAuthenticationToken;
    }

}
