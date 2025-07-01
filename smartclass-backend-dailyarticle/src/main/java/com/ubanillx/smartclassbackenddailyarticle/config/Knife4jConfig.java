package com.ubanillx.smartclassbackenddailyarticle.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智云星课-每日文章服务接口文档")
                        .version("1.0.0")
                        .description("智云星课平台每日文章服务相关接口")
                        .contact(new Contact()
                                .name("智云星课开发团队")
                                .email("dev@smartclass.com")));
    }
}
