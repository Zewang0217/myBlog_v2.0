package org.Zewang.myBlog.controller.file;

import org.Zewang.myBlog.common.ApiResponse;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/file")
@PreAuthorize("hasAnyRole('ADMIN')")
public class FileController {

    private static final String UPLOAD_DIR = "uploads/";

    public FileController() {
        // 创建上传目录
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @PostMapping("/upload")
    public ApiResponse<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }

            // 检查文件大小 (限制为10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ApiResponse.error("文件大小不能超过10MB");
            }

            // 获取文件信息
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null) {
                int lastDotIndex = originalFilename.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    fileExtension = originalFilename.substring(lastDotIndex);
                }
            }

            // 创建按日期组织的目录结构
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uploadPath = UPLOAD_DIR + datePath;
            File directory = new File(uploadPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 生成唯一文件名
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            String filePath = uploadPath + "/" + uniqueFilename;

            // 保存文件
            file.transferTo(new File(filePath));

            // 准备返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("filename", uniqueFilename);
            result.put("originalName", originalFilename);
            result.put("size", file.getSize());
            result.put("url", "/uploads/" + datePath + "/" + uniqueFilename);
            result.put("path", filePath);

            return ApiResponse.success(result);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/view/{datePath}/{filename:.+}")
    public ResponseEntity<UrlResource> viewFile(@PathVariable String datePath, @PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + datePath + "/" + filename).normalize();
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}