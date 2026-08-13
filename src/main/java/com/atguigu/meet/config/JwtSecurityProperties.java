package com.atguigu.meet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description
 * @Date 2026-05-19 16:33
 */
@Configuration
@ConfigurationProperties(prefix = "jwt.security")
@Data
public class JwtSecurityProperties {
    private List<String> publicPaths;
}
