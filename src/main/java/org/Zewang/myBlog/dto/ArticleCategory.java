package org.Zewang.myBlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章分类关联实体
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:16
 * 
 * 注意：此实体对应 article_category 表，该表只包含基本的关联字段，
 * 不包含 create_time 和 update_time 字段
 */

@Data
@Schema
public class ArticleCategory {

    @Schema(description = "关联id")
    private Long id;

    @Schema(description = "文章id")
    private Long articleId;

    @Schema(description = "分类id")
    private Long categoryId;
}