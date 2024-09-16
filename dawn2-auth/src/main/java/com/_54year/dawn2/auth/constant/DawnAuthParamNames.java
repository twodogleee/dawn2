package com._54year.dawn2.auth.constant;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * 认证服务器参数常量
 *
 * @author Anedrsen
 */
public class DawnAuthParamNames {
    /**
     * 手机短信验证码
     */
    public static final String GRANT_PHONE_CODE = "phone_auth_code";


    /*
     参数key
     */
    /**
     * 手机号的请求参数key
     */
    public static final String PARAM_PHONE_KEY = "phone_num";
    /**
     * 验证码的请求参数key
     */
    public static final String PARAM_AUTH_CODE_KEY = "auth_code";

    /**
     * 默认的访问范围
     */
    public static final String DEF_SCOPE = "all";

    /**
     * 手机验证码登录
     *
     * @return 认证类型
     */
    public static AuthorizationGrantType getPhoneGrantType() {
        return new AuthorizationGrantType(GRANT_PHONE_CODE);
    }
}
