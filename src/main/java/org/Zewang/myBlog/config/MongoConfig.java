package org.Zewang.myBlog.config;


import java.util.Arrays;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/01 22:22
 */

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        Converter<Integer, ArticleStatus> integerToArticleStatus = new Converter<Integer, ArticleStatus>() {
            @Override
            public ArticleStatus convert(Integer source) {
                return ArticleStatus.fromCode(source);
            }
        };

        Converter<ArticleStatus, Integer> articleStatusToInteger = new Converter<ArticleStatus, Integer>() {
            @Override
            public Integer convert(ArticleStatus source) {
                return source.getCode();
            }
        };

        return new MongoCustomConversions(Arrays.asList(
            integerToArticleStatus,
            articleStatusToInteger
        ));
    }
}
