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
public class FileController {

    private static final String UPLOAD_DIR;

    static {
        // 使用绝对路径，确保上传目录在应用程序根目录下
        UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
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

            // 检查文件大小 (限制为100MB)
        if (file.getSize() > 100 * 1024 * 1024) {
            return ApiResponse.error("文件大小不能超过100MB");
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
    
    /**
     * 获取所有上传的图片列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getFileList() {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                return ApiResponse.success(new HashMap<>());
            }
            
            Map<String, Object> result = new HashMap<>();
            
            // 遍历上传目录下的所有文件
            traverseUploadDir(uploadDir, result);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取文件列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 遍历上传目录，获取所有文件信息
     */
    private void traverseUploadDir(File dir, Map<String, Object> result) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归遍历子目录
                traverseUploadDir(file, result);
            } else {
                // 处理文件
            String relativePath = file.getAbsolutePath().substring(UPLOAD_DIR.length());
            // 将Windows反斜杠替换为URL正斜杠
            String urlPath = relativePath.replace(File.separator, "/");
            String datePath = relativePath.substring(0, relativePath.lastIndexOf(File.separator));
            String filename = relativePath.substring(relativePath.lastIndexOf(File.separator) + 1);
            
            // 构建文件信息
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("filename", filename);
            fileInfo.put("originalName", filename);
            fileInfo.put("size", file.length());
            fileInfo.put("url", "/uploads/" + urlPath);
            fileInfo.put("path", file.getAbsolutePath());
            fileInfo.put("datePath", datePath);
                
                // 将文件信息添加到结果中
                result.put(relativePath, fileInfo);
            }
        }
    }
}