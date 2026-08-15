package com.atguigu.meet.service.file.impl;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.config.UploadRootConfig;
import com.atguigu.meet.mapper.file.FileInfoMapper;
import com.atguigu.meet.model.entity.file.FileInfo;
import com.atguigu.meet.service.file.FileService;
import com.atguigu.meet.utils.AdminContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 文件上传 Service 实现
 * 基于 x-file-storage，支持本地/OSS/MinIO/COS/Kodo 等多种存储平台
 * 切换平台只需修改 application.yml 中 dromara.x-file-storage.default-platform
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UploadRootConfig uploadRootConfig;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Override
    public Response upload(MultipartFile file, String bizType) {
        // 1. 校验业务类型是否存在
        var typeConfig = uploadRootConfig.getTypeConfig().get(bizType);
        if (typeConfig == null) {
            return Response.fail(500, bizType + "上传业务不支持");
        }
        // 2. 校验文件是否为空
        if (file.isEmpty()) {
            return Response.fail(500, "文件为空");
        }
        // 3. 解析原始文件名、后缀
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return Response.fail(500, "文件名异常");
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        // 4. 校验文件后缀
        List<String> allowSuffixList = Arrays.asList(typeConfig.getAllowSuffix().split(","));
        if (!allowSuffixList.contains(suffix)) {
            return Response.fail(500, "仅允许上传格式：" + typeConfig.getAllowSuffix());
        }
        // 5. 校验文件大小
        long maxSize = typeConfig.getMaxSizeMb() * 1024 * 1024L;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("文件不能超过 " + typeConfig.getMaxSizeMb() + "MB");
        }
        // 6. 通过 x-file-storage 上传，自动生成外网 URL
        org.dromara.x.file.storage.core.FileInfo uploaded = fileStorageService.of(file)
                .setPath(typeConfig.getSubPath())
                .upload();
        // 7. 文件元数据入库（便于假删除/审计）
        saveFileInfo(uploaded, originalFilename, suffix, bizType, file.getSize());
        return Response.ok(200, "上传成功", uploaded.getUrl());
    }

    @Override
    public Response delete(String url) {
        if (url == null || url.isBlank()) {
            return Response.fail(500, "文件URL不能为空");
        }
        // 按 url 查询文件记录(包含已删除,避免重复删除误报)
        FileInfo record = fileInfoMapper.selectOne(
                new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getUrl, url));
        if (record == null) {
            return Response.fail(500, "文件记录不存在");
        }
        if (record.getIsDeleted() == 1) {
            return Response.fail(500, "文件已删除");
        }
        // 假删除：仅更新状态与逻辑删除标识，物理文件保留
        Long operatorId = AdminContext.getLoginUserId();
        fileInfoMapper.update(null,
                new LambdaUpdateWrapper<FileInfo>()
                        .eq(FileInfo::getId, record.getId())
                        .set(FileInfo::getStatus, 0)
                        .set(FileInfo::getIsDeleted, 1)
                        .set(FileInfo::getUpdateBy, operatorId));
        return Response.ok(200, "删除成功", null);
    }

    /**
     * 将 x-file-storage 上传返回的 FileInfo 入库
     */
    private void saveFileInfo(org.dromara.x.file.storage.core.FileInfo fi,
                              String originalFilename, String suffix,
                              String bizType, long size) {
        FileInfo entity = new FileInfo();
        entity.setUrl(fi.getUrl());
        entity.setOriginalName(originalFilename);
        entity.setFilename(fi.getFilename());
        entity.setPath(fi.getPath());
        entity.setSize(size);
        entity.setSuffix(suffix);
        entity.setBizType(bizType);
        entity.setPlatform(fi.getPlatform());
        entity.setBucket(null);
        entity.setBasePath(fi.getBasePath());
        entity.setStatus(1);
        entity.setIsDeleted(0);
        entity.setCreateBy(AdminContext.getLoginUserId());
        entity.setUpdateBy(AdminContext.getLoginUserId());
        fileInfoMapper.insert(entity);
    }
}
