package com.ljy.pbl6.controller;

import com.ljy.pbl6.common.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @PostMapping("/image")
    public Response<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Response.error(400, "请选择图片");
        }

        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        // 构建resources/static/img目录路径
        String realPath = projectRoot + "/src/main/resources/static/img/";
        File dir = new File(realPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + suffix;

        File targetFile = new File(dir, newFileName);
        file.transferTo(targetFile);

        String filePath = "/static/img/" + newFileName;

        return Response.success(filePath);
    }
}
