package com.atguigu.meet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description
 * @Date 2026-05-14 11:29
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问 /upload/** 时, 映射到本地 F:/upload/
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:F:/upload/");
    }
}
