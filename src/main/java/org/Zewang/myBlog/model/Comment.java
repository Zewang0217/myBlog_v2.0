package org.Zewang.myBlog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 评论实体类
 * @email "Zewang0217@outlook.com"
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "comments")
@Schema(description = "评论实体")
public class Comment {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Schema(description = "评论ID")
    private String id;
    
    // 添加显式的setter方法
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public void setStatus(int status) {
         this.status = status;
     }
    
    public void setLikes(Integer likes) {
        this.likes = likes;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
         this.updateTime = updateTime;
     }
     
     // 添加getter方法
     public String getArticleId() {
         return articleId;
     }
     
     public String getId() {
         return id;
     }
     
     public String getUserId() {
         return userId;
     }
     
     public String getUsername() {
         return username;
     }
     
     public String getContent() {
         return content;
     }
     
     public String getParentId() {
         return parentId;
     }
     
     public Integer getStatus() {
         return status;
     }
     
     public Integer getLikes() {
         return likes;
     }
     
     public LocalDateTime getCreateTime() {
         return createTime;
     }
     
     public LocalDateTime getUpdateTime() {
         return updateTime;
     }
    
    @Schema(description = "文章ID")
    private String articleId;
    
    @Schema(description = "用户ID")
    private String userId;
    
    @Schema(description = "用户名")
    @JsonProperty("author")
    private String username;
    
    @Schema(description = "评论内容")
    private String content;
    
    @Schema(description = "父评论ID，用于回复功能，为null表示顶级评论")
    private String parentId;
    
    @Schema(description = "评论状态：0-正常，1-已删除")
    private Integer status = 0;
    
    @Schema(description = "点赞数")
    @JsonProperty("likeCount")
    private Integer likes = 0;
    
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

    // 回复列表，非数据库字段，用于前端展示嵌套结构
    @Schema(description = "回复列表")
    private transient List<Comment> replies;

    // 回复列表的 getter 和 setter
    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }
}