package com.atguigu.meet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 上传业务校验配置（后缀、大小限制）
 * 实际文件存储由 x-file-storage 接管，这里仅保留业务校验规则
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadRootConfig {
    // 各业务类型配置
    private Map<String, UploadTypeConfig> typeConfig;
}
