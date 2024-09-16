package com._54year.dawn2.auth.service.impl;

import com._54year.dawn2.auth.dao.DawnUserInfoMapper;
import com._54year.dawn2.auth.entity.DawnUserInfo;
import com._54year.dawn2.auth.service.IDawnUserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户基础信息表 服务实现类
 * </p>
 *
 * @author Andersen
 * @since 2024-09-16
 */
@Service
public class DawnUserInfoServiceImpl extends ServiceImpl<DawnUserInfoMapper, DawnUserInfo> implements IDawnUserInfoService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据用户名查询用户信息
     *
     * @param username the username identifying the user whose data is required.
     * @return 用户信息
     * @throws UsernameNotFoundException 用户未找到
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<DawnUserInfo> queryWrapper = Wrappers.<DawnUserInfo>lambdaQuery()
                .eq(DawnUserInfo::getUsername, username);
        return this.getOne(queryWrapper);
    }

    /**
     * 根据手机号查询
     *
     * @param phone
     * @return
     */
    public DawnUserInfo getUserInfoByPhone(String phone) {
        LambdaQueryWrapper<DawnUserInfo> queryWrapper = Wrappers.<DawnUserInfo>lambdaQuery()
                .eq(DawnUserInfo::getPhone, phone);
        return this.getOne(queryWrapper);
    }
}
