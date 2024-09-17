package com._54year.dawn2.auth.filter;

import com._54year.dawn2.auth.constant.DawnAuthUrl;
import com._54year.dawn2.auth.util.DawnAuthUtils;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * 全局拦截器 对请求参数进行改写 支持json参数认证
 *
 * @author Andersen
 * {@link OAuth2TokenEndpointFilter}
 */
@Slf4j
public class DawnTokenParamFilter extends OncePerRequestFilter {

    //token请求路径拦截
    RequestMatcher requestMatcher = new AntPathRequestMatcher(DawnAuthUrl.TOKEN_URL, HttpMethod.POST.name());

    /**
     * 拦截方法执行
     *
     * @param request     请求
     * @param response    返回
     * @param filterChain 拦截链
     * @throws ServletException 服务异常
     * @throws IOException      io异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        //请求类型
        String type = request.getContentType();
        //如果请求类型为json
        if (MediaType.APPLICATION_JSON_VALUE.equals(type) //请求类型为json
                && ObjectUtils.isEmpty(request.getParameterMap()) //from表单参数为空
        ) {
            JSONObject jsonBody = DawnAuthUtils.getJsonBody(request);
            Map<String, String[]> nowParam = DawnAuthUtils.getParameters(jsonBody);
            request = DawnAuthUtils.modifiedRequest(nowParam, request);
        }
        //放行
        filterChain.doFilter(request, response);
    }


}
