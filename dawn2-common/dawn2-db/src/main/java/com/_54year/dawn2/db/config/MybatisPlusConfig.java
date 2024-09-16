package com._54year.dawn2.db.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;


@Configuration
//设置mapper扫描路径 扫描项目中所有路径下的dao
@MapperScan({"com._54year.dawn2.*.dao"})
@Slf4j
public class MybatisPlusConfig {
    @PostConstruct
    public void init() {
        log.info("Dawn2-DB=>>>>>MybatisPlusConfig init");
    }

}
