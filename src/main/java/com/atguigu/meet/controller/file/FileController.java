package com.atguigu.meet.controller.file;

import com.atguigu.meet.annotation.RequirePermission;
import com.atguigu.meet.common.Response;
import com.atguigu.meet.constant.PermissionConst;
import com.atguigu.meet.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理接口
 *
 * @Description
 * @Date 2026-08-13 9:39
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    public FileService fileService;

    /**
     * 上传文件(通用接口,后端内部/前端直传均可)
     */
    @PostMapping("upload")
    @RequirePermission(PermissionConst.FILE_UPLOAD)
    public Response upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bizType") String bizType) {
        try {
            return fileService.upload(file, bizType);
        } catch (RuntimeException e) {
            return Response.fail(500, e.getMessage());
        }
    }

    /**
     * 删除文件(假删除,仅更新 t_file_info 状态,物理文件保留可恢复)
     *
     * @param url 文件访问URL
     */
    @DeleteMapping
    @RequirePermission(PermissionConst.FILE_DELETE)
    public Response delete(@RequestParam("url") String url) {
        return fileService.delete(url);
    }
}
