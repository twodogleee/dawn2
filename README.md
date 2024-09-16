# dawn2

## 简介

整体项目才用比较新spring版本进行重新开发,项目管理换成gradle,并整合了springOAuth2

- springBoot: 3.2.0
- springCloud: 2023.0.0
- springSecurity: 6.2.0
- springOAuth2Server: 1.2.0
- jdk:17


## 项目结构

```angular2html

├── dawn2-auth //认证服务,整合oauth2
├── dawn2-common //基础模块
│   ├── dawn2-core //一些公共得常量,方法
│   └── dawn2-db //数据库插件,mybatisPlus+多数据源
├── dawn2-gateway //网关服务
├── dawn2-generator //CURD代码生成

```