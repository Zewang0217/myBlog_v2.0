package org.Zewang.myBlog.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.Zewang.myBlog.model.enums.ArticleStatus;

import java.io.IOException;

/**
 * ArticleStatus 枚举序列化器
 * 将枚举序列化为数字代码，便于前端处理
 */
public class ArticleStatusSerializer extends JsonSerializer<ArticleStatus> {

    @Override
    public void serialize(ArticleStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // 序列化为数字代码
            gen.writeNumber(value.toCode());
        } else {
            gen.writeNull();
        }
    }
}