package com._54year.dawn2.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * <p>
 * 用户基础信息表
 * </p>
 *
 * @author Andersen
 * @since 2024-09-16
 */
@JsonTypeName("UserInfo")
@TableName("dawn_user_info")
@Data
public class DawnUserInfo implements UserDetails, CredentialsContainer, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId("user_id")
    private Long userId;
    /**
     * 租户id
     */
    @TableField("tenant_id")
    private Long tenantId;
    /**
     * 用户登录名
     */
    @TableField("username")
    private String username;
    /**
     * 用户邮箱
     */
    @TableField("email")
    private String email;
    /**
     * 用户电话
     */
    @TableField("phone")
    private String phone;
    /**
     * 用户密码
     */
    @TableField("password")
    private String password;
    /**
     * 角色数组
     */
    @TableField(exist = false)
    private Set<GrantedAuthority> authorities;
    /**
     * 账户未过期 true未过期 false已过期
     * 目前数据库未存该字段 默认为true
     */
    @TableField("account_non_expired")
    private boolean accountNonExpired = true;
    /**
     * 账户未锁定 true未锁定 false已锁定
     * 目前数据库未存该字段 默认为true
     */
    @TableField("account_non_locked")
    private boolean accountNonLocked = true;
    /**
     * 凭据未过期 true未过期 false已过期
     * 目前未存该字段 默认为true
     */
    @TableField("credentials_non_expired")
    private boolean credentialsNonExpired = true;
    /**
     * 是否启用
     */
    @TableField("enabled")
    private boolean enabled = true;


    /**
     * 用于在认证后清除敏感信息
     */
    @Override
    public void eraseCredentials() {
        setPassword(null);
    }
}
