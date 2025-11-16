package org.Zewang.myBlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 创建评论的数据传输对象
 * @email "Zewang0217@outlook.com"
 */
@Data
@Schema(description = "创建评论的数据传输对象")
public class CreateCommentDTO {
    @NotBlank(message = "文章ID不能为空")
    @Schema(description = "文章ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String articleId;
    
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 500, message = "评论内容长度必须在1-500个字符之间")
    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "这篇文章写得真好！")
    private String content;
    
    @Schema(description = "父评论ID，用于回复功能，为null表示顶级评论")
    private String parentId;
    
    // 添加显式的getter方法
    public String getArticleId() {
        return articleId;
    }
    
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}