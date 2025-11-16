package org.Zewang.myBlog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 点赞实体类
 * @email "Zewang0217@outlook.com"
 */
@Data
@Accessors(chain = true)
@Document(collection = "likes")
@Schema(description = "点赞实体")
public class Like {
    
    @Id
    @Schema(description = "点赞ID")
    private String id;
    
    @Schema(description = "用户ID")
    private String userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "文章ID")
    private String articleId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}