package com._54year.dawn2.auth.service;

import com._54year.dawn2.auth.entity.DawnUserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * <p>
 * 用户基础信息表 服务类
 * </p>
 *
 * @author Andersen
 * @since 2024-09-16
 */
public interface IDawnUserInfoService extends IService<DawnUserInfo>, UserDetailsService {
    /**
     * 根据用户名查询用户信息
     *
     * @param username the username identifying the user whose data is required.
     * @return 用户信息
     * @throws UsernameNotFoundException 用户未找到
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * 根据手机号查询
     *
     * @param phone
     * @return
     */
    DawnUserInfo getUserInfoByPhone(String phone);

}
