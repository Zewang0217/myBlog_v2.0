package org.Zewang.myBlog.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 配置类，用于支持跨域请求
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/23 18:12
 */

@Configuration
public class WebConfig {

    @Bean // 作用：注册bean对象；
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
        @Override
            public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                .allowedOriginPatterns("*") // 允许所有源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
            }
        };
    }
}
