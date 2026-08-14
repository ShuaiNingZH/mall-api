package com.atguigu.meet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 权限缓存配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "permission")
public class PermissionCacheProperties {

    /**
     * Redis 缓存前缀
     */
    private String cachePrefix = "meet:permission:";

    /**
     * 缓存过期时间（秒），默认 1 小时
     */
    private long expireSeconds = 3600;
}
