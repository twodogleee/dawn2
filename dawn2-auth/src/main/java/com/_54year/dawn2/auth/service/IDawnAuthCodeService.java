package com._54year.dawn2.auth.service;

import com._54year.dawn2.auth.entity.DawnAuthCode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 验证码记录表 服务类
 * </p>
 *
 * @author Andersen
 * @since 2024-09-11
 */
public interface IDawnAuthCodeService extends IService<DawnAuthCode> {

    /**
     * 根据认证id查询认证code
     *
     * @param authId 认证id phone email
     * @return 认证码
     */
    DawnAuthCode getAuthCode(String authId);

    /**
     * 判断 authCode是否有效 比如是否过期/是否被使用了等等
     *
     * @param dawnAuthCode code信息
     * @return ture有效
     */
    boolean isActive(DawnAuthCode dawnAuthCode);

}
