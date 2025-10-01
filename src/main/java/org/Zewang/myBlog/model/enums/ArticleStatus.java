package org.Zewang.myBlog.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    /**
     * 用于将JSON中的数字值转换为枚举
     * @param code 状态码
     * @return 对应的枚举值
     */
    @JsonCreator
    public static ArticleStatus fromCode(int code) {
        for (ArticleStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        // 默认返回草稿状态
        return DRAFT;
    }

    /**
     * 用于将枚举值转换为数字值进行JSON序列化
     * @return 状态码
     */
    @JsonValue
    public int toCode() {
        return code;
    }
}