package com._54year.dawn2.auth.granter;

import com._54year.dawn2.auth.constant.DawnAuthMsg;
import com._54year.dawn2.auth.constant.DawnAuthParamNames;
import com._54year.dawn2.core.exception.DawnAuthException;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

/**
 * 手机验证码授权转换
 *
 * @author Andersen
 */
public class PhoneAuthenticationConverter extends AbstractDawnGrantAuthenticationConverter {
    @Override
    public boolean grantTypeCheck(String grantType) {
        return DawnAuthParamNames.GRANT_PHONE_CODE.equals(grantType);
    }

    @Override
    public void additionalParametersCheck(Map<String, String> parameters) {
        String authCode = parameters.get(DawnAuthParamNames.PARAM_PHONE_KEY);
        String phoneNum = parameters.get(DawnAuthParamNames.PARAM_AUTH_CODE_KEY);
        if (ObjectUtils.isEmpty(authCode) || ObjectUtils.isEmpty(phoneNum)) {
            throw new DawnAuthException(DawnAuthMsg.PARAM_EMPTY);
        }
    }
}
