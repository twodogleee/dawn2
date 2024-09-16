package com._54year.dawn2.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 验证码记录表
 * </p>
 *
 * @author Andersen
 * @since 2024-09-11
 */
@Getter
@Setter
@TableName("dawn_auth_code")
public class DawnAuthCode implements Serializable  {

    private static final long serialVersionUID = 1L;

    /**
     * 认证的id,手机号邮箱等
     */
    @TableId("auth_id")
    private String authId;

    /**
     * 验证码
     */
    @TableField("auth_code")
    private String authCode;

    /**
     * 类型 1手机 2邮箱
     */
    @TableField("type")
    private Integer type;

}
