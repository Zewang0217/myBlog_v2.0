package org.Zewang.myBlog.model.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.Zewang.myBlog.model.enums.ArticleStatus;

import java.io.IOException;

/**
 * ArticleStatus 枚举反序列化器
 * 支持从数字和字符串两种形式反序列化
 */
public class ArticleStatusDeserializer extends JsonDeserializer<ArticleStatus> {

    @Override
    public ArticleStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonParser currentParser = p;
        
        try {
            // 首先尝试作为数字读取
            if (currentParser.currentToken().isNumeric()) {
                int code = currentParser.getIntValue();
                return ArticleStatus.fromCode(code);
            } else {
                // 尝试作为字符串读取
                String value = currentParser.getText();
                if (value != null && !value.trim().isEmpty()) {
                    return ArticleStatus.fromValue(value);
                }
            }
        } catch (Exception e) {
            // 如果转换失败，返回默认值
            return ArticleStatus.DRAFT;
        }
        
        return ArticleStatus.DRAFT;
    }
}