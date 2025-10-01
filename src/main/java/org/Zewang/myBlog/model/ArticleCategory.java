package org.Zewang.myBlog.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:27
 */

@Data
@Accessors(chain = true) // 作用：链式编程（setter返回当前对象）
@Schema(description = "文章分类关联表")
public class ArticleCategory {
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "分类ID")
    private Long categoryId;
}
