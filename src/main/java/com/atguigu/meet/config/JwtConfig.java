package com.atguigu.meet.config;

import com.atguigu.meet.utils.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Description
 * @Date 2026-05-15 14:36
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String SECRET_KEY_STR;

    private long EXPIRE_TIME;

    @Bean
    @Primary
    public JwtUtil jwtUtil() {
        return new JwtUtil(SECRET_KEY_STR, EXPIRE_TIME);
    }
}
