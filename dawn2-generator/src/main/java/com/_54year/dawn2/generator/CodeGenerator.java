package com._54year.dawn2.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;
import java.util.Collections;

/**
 * 代码生成器
 *
 * @author Andersen
 */
public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://192.168.5.55:9001/dawn2_auth?serverTimezone=GMT%2B8", "root", "zhenghao")
                .globalConfig(builder -> {
                    builder.author("Andersen") // 设置作者
                            .outputDir("E:\\workspace\\dawn2\\dawn2-generator\\codeout"); // 输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com._54year.dawn2.auth") // 设置父包名
                            .entity("entity") // 设置实体类包名
                            .mapper("dao") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("dao.mapper"); // 设置 Mapper XML 文件包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude("dawn_user_info")// 设置需要生成的表名
//                            .addTablePrefix("tl_")
                            .entityBuilder()
                            .enableLombok() // 启用 Lombok
                            .enableTableFieldAnnotation() // 启用字段注解
                            .controllerBuilder()
                            .enableRestStyle(); // 启用 REST 风格
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }
}
