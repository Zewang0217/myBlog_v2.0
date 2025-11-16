package org.Zewang.myBlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 创建或更新分类的数据传输对象
 * @email "Zewang0217@outlook.com"
 */
@Schema(description = "创建或更新分类的数据传输对象")
public record CreateCategoryDTO(
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "技术博客")
    String name,
    
    @Size(max = 200, message = "分类描述长度不能超过200个字符")
    @Schema(description = "分类描述", example = "分享技术相关的文章")
    String description
) {}