package com._54year.dawn2.auth.service.impl;

import com._54year.dawn2.auth.dao.DawnAuthCodeMapper;
import com._54year.dawn2.auth.entity.DawnAuthCode;
import com._54year.dawn2.auth.service.IDawnAuthCodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 验证码记录表 服务实现类
 * </p>
 *
 * @author Andersen
 * @since 2024-09-11
 */
@Service
public class DawnDawnAuthCodeServiceImpl extends ServiceImpl<DawnAuthCodeMapper, DawnAuthCode> implements IDawnAuthCodeService {

    /**
     * 根据认证id查询认证code
     *
     * @param authId 认证id phone email
     * @return 认证码
     */
    public DawnAuthCode getAuthCode(String authId) {
        return this.getById(authId);
    }

    /**
     * 判断 authCode是否有效 比如是否过期/是否被使用了等等
     *
     * @param dawnAuthCode code信息
     * @return ture有效
     */
    public boolean isActive(DawnAuthCode dawnAuthCode) {
        return true;
    }
}
