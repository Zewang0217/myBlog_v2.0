package org.Zewang.myBlog.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: Swagger的相关配置，定义api文档的基本信息
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/01 15:48
 */

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI blogAPI() {
        return new OpenAPI()
            .info(new Info().title("我的博客API")
            .description("我的博客API文档")
            .version("2.1.0")
            .license(new License().name("Apache 2.0")
                .url("http://springdoc.org")))
            .externalDocs(new ExternalDocumentation()
                .description("博客项目文档")
                .url("https://github.com/Zewang0217/myBlog_v2.0"));
    }
}
