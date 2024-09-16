package com._54year.dawn2.auth.granter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 框架自带的用户名密码处理器
 * 用来处理UsernamePasswordAuthenticationToken
 * 继承DaoAuthenticationProvider并交给spring管理
 * 参照 {@link AbstractUserDetailsAuthenticationProvider}
 */
//@Primary //提高改类的优先级
//@Component
//@Slf4j
public class DawnPasswordAuthenticationProvider extends DaoAuthenticationProvider {


    /**
     * 身份认证 可以添加一下自己需要的额外认证
     *
     * @param userDetails    as retrieved from the
     *                       {@link #retrieveUser(String, UsernamePasswordAuthenticationToken)} or
     *                       <code>UserCache</code>
     * @param authentication the current request that needs to be authenticated
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
