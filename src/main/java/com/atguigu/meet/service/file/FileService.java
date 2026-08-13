package com.atguigu.meet.service.file;

import com.atguigu.meet.common.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Date 2026-08-13 0:00
 */
public interface FileService {
    /**
     * 通用文件上传
     *
     * @param file    文件
     * @param bizType 业务类型: avatar / goods / document
     * @return 最终访问路径
     */
    Response upload(MultipartFile file, String bizType);
}
