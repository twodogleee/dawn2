package com._54year.dawn2.auth.granter;

import com._54year.dawn2.auth.constant.DawnAuthMsg;
import com._54year.dawn2.auth.constant.DawnAuthParamNames;
import com._54year.dawn2.auth.entity.DawnAuthCode;
import com._54year.dawn2.auth.entity.DawnUserInfo;
import com._54year.dawn2.auth.service.IDawnAuthCodeService;
import com._54year.dawn2.auth.service.IDawnUserInfoService;
import com._54year.dawn2.auth.util.DawnAuthUtils;
import com._54year.dawn2.core.exception.DawnAuthException;

import java.util.Map;

/**
 * 手机验证码授权提供
 *
 * @author Andersen
 */
public class PhoneAuthenticationProvider extends AbstractDawnAuthenticationProvider {


    @Override
    public DawnUserInfo loadUserInfo(DawnAuthenticationToken dawnAuthenticationToken, IDawnUserInfoService dawnUserService, IDawnAuthCodeService authCodeService) {
        Map<String, String> parameters = dawnAuthenticationToken.getAdditionalParameters();
        String phoneNum = parameters.get(DawnAuthParamNames.PARAM_PHONE_KEY);
        String authCode = parameters.get(DawnAuthParamNames.PARAM_AUTH_CODE_KEY);

        //获取验证码
        DawnAuthCode code = authCodeService.getAuthCode(phoneNum);
        DawnAuthUtils.isEmptyThrowAuth(code, DawnAuthMsg.AUTH_CODE_EMPTY);

        //验证码错误
        if (!authCode.equals(code.getAuthCode())) {
            throw new DawnAuthException(DawnAuthMsg.AUTH_CODE_ERROR);
        }

        //用手机查询用户信息
        DawnUserInfo userInfo = dawnUserService.getUserInfoByPhone(phoneNum);
        DawnAuthUtils.isEmptyThrowAuth(code, DawnAuthMsg.AUTH_USER_EMPTY);

        return userInfo;
    }
}
