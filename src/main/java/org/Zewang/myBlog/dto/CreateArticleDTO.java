package org.Zewang.myBlog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import java.util.List;

@Schema(description = "创建或更新文章的数据传输对象")
public record CreateArticleDTO(
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    @Schema(description = "文章标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "我的第一篇文章")
    String title,

    @NotBlank(message = "内容不能为空")
    @Schema(description = "文章内容", requiredMode = Schema.RequiredMode.REQUIRED)
    String content,

    @NotBlank(message = "作者不能为空")
    @Size(max = 50, message = "作者名长度不能超过50个字符")
    @Schema(description = "作者", example = "Zewang")
    String author,

    @Schema(description = "文章状态")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    ArticleStatus status,

    @Schema(description = "文章分类ID列表")
    List<String> categoryIds
) {}
