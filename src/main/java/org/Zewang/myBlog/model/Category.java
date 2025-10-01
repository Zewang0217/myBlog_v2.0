package org.Zewang.myBlog.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章分类的实体类
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:14
 */

@Data
@Accessors(chain = true)
@Schema(description = "文章分类实体")
public class Category {
    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 设置时间格式
    private String createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;

}
