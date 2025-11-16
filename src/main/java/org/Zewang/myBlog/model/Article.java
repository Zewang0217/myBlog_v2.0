package org.Zewang.myBlog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.Zewang.myBlog.model.serialize.ArticleStatusSerializer;
import org.Zewang.myBlog.model.serialize.ArticleStatusDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import org.Zewang.myBlog.model.enums.ArticleStatus;

/**
 * @author "Zewang"
 * @version 2.0
 * @description: 文章实体类 (MongoDB 版本)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/23 21:30
 */
@Accessors(chain = true)
@Document(collection = "articles")
@Schema(description = "文章实体")
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
public class Article {
    @Id
    @Schema(description = "文章ID")
    private String id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    @Schema(description = "状态")
    @JsonSerialize(using = ArticleStatusSerializer.class)
    @JsonDeserialize(using = ArticleStatusDeserializer.class)
    private ArticleStatus status;
    
    @Schema(description = "评论数")
    private Integer commentCount = 0;
    
    @Schema(description = "点赞数")
    private Integer likeCount = 0;

    @Schema(description = "文章分类列表")
    private List<Category> categories;
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public Article setId(String id) {
        this.id = id;
        return this;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Article setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public String getContent() {
        return content;
    }
    
    public Article setContent(String content) {
        this.content = content;
        return this;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public Article setAuthor(String author) {
        this.author = author;
        return this;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public Article setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public Article setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    
    public ArticleStatus getStatus() {
        return status;
    }
    
    public Article setStatus(ArticleStatus status) {
        this.status = status;
        return this;
    }
    
    public Integer getCommentCount() {
        return commentCount;
    }
    
    public Article setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
        return this;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public Article setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
        return this;
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public Article setCategories(List<Category> categories) {
        this.categories = categories;
        return this;
    }
}
