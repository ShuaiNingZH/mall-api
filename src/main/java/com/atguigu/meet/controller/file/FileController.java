package com.atguigu.meet.controller.file;

import com.atguigu.meet.common.Response;
import com.atguigu.meet.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Date 2026-08-13 9:39
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    public FileService fileService;

    @PostMapping("upload")
    public Response upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bizType") String bizType
    ) {
        try {
            return fileService.upload(file, bizType);
        } catch (RuntimeException e) {
            return Response.fail(500, e.getMessage());
        }
    }
}
