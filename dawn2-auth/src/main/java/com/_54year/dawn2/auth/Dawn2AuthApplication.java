package com._54year.dawn2.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com._54year.dawn2.*") //将依赖包也扫描进来
public class Dawn2AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(Dawn2AuthApplication.class, args);
    }

}
