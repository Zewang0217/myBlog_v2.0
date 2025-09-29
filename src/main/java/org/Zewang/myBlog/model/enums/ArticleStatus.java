package org.Zewang.myBlog.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

/**
 * 文章状态枚举
 */

@Getter // 获取枚举属性的值
public enum ArticleStatus {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下架");

    private final int code;
    private final String description;

    ArticleStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator // 创建一个从代码到枚举的映射
    public static ArticleStatus fromCode(int code) { // fromCode: 从代码获取状态
        for (ArticleStatus status : values()) {
            if (status.code == code) { // 匹配状态代码，返回状态
                return status;
            }
        }
        return DRAFT; // 默认为草稿
    }

}
