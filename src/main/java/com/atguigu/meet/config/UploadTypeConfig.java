package com.atguigu.meet.config;

import lombok.Data;

/**
 * @Description
 * @Date 2026-05-14 9:49
 */
@Data
public class UploadTypeConfig {
    // 子目录
    private String subPath;
    // 允许后缀
    private String allowSuffix;
    // 最大大小 MB
    private Integer maxSizeMb;
}
