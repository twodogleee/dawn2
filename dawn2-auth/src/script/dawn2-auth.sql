-- 验证码存储
create table dawn_auth_code
(
    auth_id   varchar(255) not null comment '认证的id,手机号邮箱等'
        primary key,
    auth_code varchar(20) null comment '验证码',
    type      int null comment '类型 1手机 2邮箱'
) comment '验证码记录表';

-- 用户信息表
create table dawn_user_info
(
    user_id                 bigint       not null comment '用户id'
        primary key,
    tenant_id               bigint       not null comment '租户id',
    username                varchar(255) not null comment '用户登录名',
    email                   varchar(255) null comment '用户邮箱',
    phone                   varchar(255) null comment '用户电话',
    password                varchar(255) null comment '用户密码',
    account_non_expired     tinyint null comment '账户未过期 true未过期 false已过期',
    account_non_locked      tinyint null comment '账户未锁定 true未锁定 false已锁定',
    credentials_non_expired tinyint null comment '凭据未过期 true未过期 false已过期',
    enabled                 tinyint null comment '是否启用'
) comment '用户基础信息表';