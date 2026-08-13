package com.atguigu.meet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description
 * @Date 2026-05-14 9:52
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadRootConfig {
    // 根路径
    private String rootPath;
    // 各业务类型配置
    private Map<String, UploadTypeConfig> typeConfig;
}
