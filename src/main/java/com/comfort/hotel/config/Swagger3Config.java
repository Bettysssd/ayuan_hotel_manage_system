package com.comfort.hotel.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import springfox.documentation.service.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * @Author: ayuan
 * @PS: 配置swagger文档
 * @Date: 2024/8/30 12:02
 */

@Configuration
@EnableKnife4j // 开启knife4j
public class Swagger3Config {
    @Bean
    public Docket systemDocket() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("系统管理")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.comfort.hotel.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("悦居酒店管理系统swagger文档")
                .description("悦居酒店管理系统swagger文档")
                .version("1.0.0")
                .contact(new Contact("ayuan", "https://gitee.com/hangtianRange", "18319720112@163.com"))
                .termsOfServiceUrl("https://i.csdn.net/#/user-center/profile?spm=1000.2115.3001.5111")
                .build();
    }
}