package com.atguigu.meet.service.file.impl;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.config.UploadRootConfig;
import com.atguigu.meet.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Date 2026-08-13 0:00
 */

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private UploadRootConfig uploadRootConfig;

    @Override
    public Response upload(MultipartFile file, String bizType) {
        // 1. 效验业务类型是否存在
        var typeConfig = uploadRootConfig.getTypeConfig().get(bizType);
        if (typeConfig == null) {
            return Response.fail(500, bizType + "上传业务不支持");
        }
        // 2. 效验文件是否为空
        if (file.isEmpty()) {
            return Response.fail(500, "文件为空");
        }
        // 3. 解析原始文件名, 后缀
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return Response.fail(500, "文件名异常");
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        // 4. 效验文件后缀
        List<String> allowSuffixList = Arrays.asList(typeConfig.getAllowSuffix().split(","));
        if (!allowSuffixList.contains(suffix)) {
            return Response.fail(500, "仅允许上传格式：" + typeConfig.getAllowSuffix());
        }
        // 5. 效验文件大小
        long maxSize = typeConfig.getMaxSizeMb() * 1024 * 1024L;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("文件不能超过 " + typeConfig.getMaxSizeMb() + "MB");
        }
        // 6. 生成新文件名 UUID 防覆盖
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
        // 7. 拼接完整保存路径
        String fullSavePath = uploadRootConfig.getRootPath() + typeConfig.getSubPath();
        File folder = new File(fullSavePath);
        // 不存在自动建目录
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 8. 保存文件
        File destFile = new File(folder, newFileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }
        return Response.ok(200, "上传成功", "/upload/" + typeConfig.getSubPath() + newFileName);
    }
}


