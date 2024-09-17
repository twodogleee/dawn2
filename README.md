# dawn2

## 简介

整体项目采用目前新spring版本进行重新开发,项目管理换成gradle,并整合了最新的springOAuth2

- springBoot: 3.3.3
- springCloud: 2023.0.3
- springOauth2AuthorizationServer: 3.3.3
- jdk:17


## 项目结构

```angular2html
├── build.gradle //spring boot/cloud jdk等基本设置
├── config.gradle //额外得依赖包版本管理
├── setting.gradle //项目管理
├── dawn2-auth //认证服务,整合oauth2
├── dawn2-common //基础模块
│   ├── dawn2-core //一些公共得常量,方法
│   └── dawn2-db //数据库插件,mybatisPlus+多数据源
├── dawn2-gateway //网关服务
├── dawn2-generator //CURD代码生成

```

## 其他说明

### dawn2-auth 认证模块
认证模块只提供基础的oauth2 token下发流程,不做用户的权限校验等.
则用户中心只保存最基本的用户信息,用户扩增信息及权限管理等由子模块提供,如果需要可自行实现.更详细的说明见`dawn2-auth/README`