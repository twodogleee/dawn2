package com._54year.dawn2.db.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//设置mapper扫描路径
@MapperScan("com._54year.dawn2.*.dao")
public class MybatisPlusConfig {

}
