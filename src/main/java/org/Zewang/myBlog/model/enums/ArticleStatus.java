package org.Zewang.myBlog.model.enums;

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

    public static ArticleStatus fromCode(int code) {
        for (ArticleStatus status : values()) {
            if (status.code == code) { // 匹配状态代码，返回状态
                return status;
            }
        }
        return DRAFT; // 默认为草稿
    }

}
