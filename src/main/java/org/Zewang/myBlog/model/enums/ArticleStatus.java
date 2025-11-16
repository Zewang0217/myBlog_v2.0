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

    ArticleStatus(int code) {
        this(code, "");
    }

    ArticleStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 用于将JSON中的数字值或字符串值转换为枚举
     * @param value 状态值（数字或字符串）
     * @return 对应的枚举值
     */
    @JsonCreator
    public static ArticleStatus fromValue(Object value) {
        if (value == null) {
            return DRAFT;
        }
        
        if (value instanceof Integer) {
            int code = (Integer) value;
            for (ArticleStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
        } else if (value instanceof String) {
            String strValue = ((String) value).trim().toUpperCase();
            
            // 直接按名称匹配（处理MongoDB中的字符串状态值）
            for (ArticleStatus status : values()) {
                if (status.name().equals(strValue)) {
                    return status;
                }
            }
            
            // 尝试按代码匹配
            try {
                int code = Integer.parseInt(strValue);
                for (ArticleStatus status : values()) {
                    if (status.code == code) {
                        return status;
                    }
                }
            } catch (NumberFormatException ex) {
                // 忽略，继续执行
            }
        }
        // 默认返回草稿状态
        return DRAFT;
    }
    
    /**
     * 用于Spring Data MongoDB的字符串转换
     */
    public static ArticleStatus fromString(String value) {
        return fromValue(value);
    }
    
    /**
     * 用于Spring Data MongoDB的整数转换
     */
    public static ArticleStatus fromCode(Integer value) {
        return fromValue(value);
    }

    /**
     * 用于将枚举值转换为数字值进行JSON序列化
     * @return 状态码
     */
    @JsonValue
    public int toCode() {
        return code;
    }
    
    /**
     * 获取枚举名称，用于MongoDB存储
     */
    public String getName() {
        return this.name();
    }
}