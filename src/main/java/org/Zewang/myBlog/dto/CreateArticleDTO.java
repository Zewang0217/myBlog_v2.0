package org.Zewang.myBlog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateArticleDTO(
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    String title,

    @NotBlank(message = "内容不能为空")
    @Size(max = 5000, message = "内容长度不能超过5000个字符")
    String content,

    @NotBlank(message = "作者不能为空")
    @Size(max = 50, message = "作者名长度不能超过50个字符")
    String author
) { }
