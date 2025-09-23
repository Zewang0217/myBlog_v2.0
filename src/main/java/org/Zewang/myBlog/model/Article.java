package org.Zewang.myBlog.model;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章的实体类  =》 对照数据库
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/21 22:55
 */

@Data
public class Article {
    // 属性
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
