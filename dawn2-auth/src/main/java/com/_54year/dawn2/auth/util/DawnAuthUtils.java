package com._54year.dawn2.auth.util;

import com._54year.dawn2.auth.constant.DawnAuthMsg;
import com._54year.dawn2.core.exception.DawnAuthException;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class DawnAuthUtils {
    private DawnAuthUtils() {
        // 禁止实例化工具类
        throw new UnsupportedOperationException("classes cannot be instantiated.");
    }

    /**
     * 提取请求中的参数并转为一个map返回
     *
     * @param request 当前请求
     * @return 请求中的参数
     */
    public static Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> parameters = new HashMap<>();
        parameterMap.forEach((key, values) -> {
            if (ObjectUtils.isNotEmpty(values)) {
                parameters.put(key, values[0]);
            }
        });
        return parameters;
    }

    /**
     * json转httpRequest中的parameter
     *
     * @param jsonBody json请求
     */
    public static Map<String, String[]> getParameters(JSONObject jsonBody) {
        Map<String, String[]> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : jsonBody.entrySet()) {
            if (ObjectUtils.isEmpty(entry.getValue())) {
                continue;
            }
            map.put(entry.getKey(), new String[]{entry.getValue().toString()});
        }
        return map;
    }

    /**
     * 获取client的认证信息
     *
     * @param authentication 认证信息
     * @return 客户端的认证信息
     */
    public static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    /**
     * 强行修改请求参数
     *
     * @param modifiedParameters 将要修改的请求参数
     * @param request            原请求
     * @return 包装后的请求
     */
    public static HttpServletRequest modifiedRequest(Map<String, String[]> modifiedParameters, HttpServletRequest request) {
        // 使用参数包装器修改请求参数
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                String[] values = modifiedParameters.get(name);
                return (values != null && values.length > 0) ? values[0] : null;
            }

            @Override
            public String[] getParameterValues(String name) {
                return modifiedParameters.get(name);
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return modifiedParameters;
            }
        };
    }

    /**
     * 从request中获取json参数
     *
     * @param request 请求
     * @return json参数
     */
    public static JSONObject getJsonBody(HttpServletRequest request) {
        StringBuilder requestBody = new StringBuilder();
        JSONObject param;
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            param = JSONObject.parseObject(requestBody.toString());
        } catch (Exception e) {
            throw new DawnAuthException(DawnAuthMsg.PARAM_EMPTY);
        }
        return param;
    }

    /**
     * 为空判断
     *
     * @param object 参数
     * @param msg    为空返回的msg
     */
    public static void isEmptyThrowAuth(Object object, String msg) {
        if (ObjectUtils.isEmpty(object)) throw new DawnAuthException(msg);
    }
}
