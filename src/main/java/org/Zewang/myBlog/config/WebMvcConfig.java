package org.Zewang.myBlog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC配置类，用于配置静态资源访问
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置/uploads目录为静态资源目录，允许直接访问
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir)
                .setCachePeriod(3600); // 设置缓存时间为1小时
    }
}