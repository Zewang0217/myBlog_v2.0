package org.Zewang.myBlog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import java.util.Arrays;
import java.util.List;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MongoDB配置类
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);
    
    /**
     * 配置MongoDB自定义转换器
     */
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        log.info("MongoDB自定义转换器配置中...");
        List<Converter<?, ?>> converters = Arrays.asList(
            new StringToArticleStatusConverter(),
            new IntegerToArticleStatusConverter(),
            new LongToArticleStatusConverter(),
            new StringToIntegerConverter(), // 新增：字符串到整数的转换器
            new ArticleStatusToStringConverter()
        );
        log.info("MongoDB自定义转换器已配置，包含类型转换支持");
        return new MongoCustomConversions(converters);
    }
    
    /**
     * 字符串到ArticleStatus的转换器
     */
    @ReadingConverter
    public static class StringToArticleStatusConverter implements Converter<String, ArticleStatus> {
        @Override
        public ArticleStatus convert(String source) {
            if (source == null) {
                return ArticleStatus.DRAFT;
            }
            log.debug("尝试将字符串转换为ArticleStatus: {}", source);
            return ArticleStatus.fromValue(source);
        }
    }
    
    /**
     * 整数到ArticleStatus的转换器
     */
    @ReadingConverter
    public static class IntegerToArticleStatusConverter implements Converter<Integer, ArticleStatus> {
        @Override
        public ArticleStatus convert(Integer source) {
            if (source == null) {
                return ArticleStatus.DRAFT;
            }
            log.debug("尝试将整数转换为ArticleStatus: {}", source);
            return ArticleStatus.fromValue(source);
        }
    }
    
    /**
     * 长整数到ArticleStatus的转换器
     */
    @ReadingConverter
    public static class LongToArticleStatusConverter implements Converter<Long, ArticleStatus> {
        @Override
        public ArticleStatus convert(Long source) {
            if (source == null) {
                return ArticleStatus.DRAFT;
            }
            log.debug("尝试将长整数转换为ArticleStatus: {}", source);
            return ArticleStatus.fromValue(source.intValue());
        }
    }
    
    /**
     * 字符串到整数的转换器 - 解决"DRAFT"到整数的转换问题
     */
    @ReadingConverter
    public static class StringToIntegerConverter implements Converter<String, Integer> {
        @Override
        public Integer convert(String source) {
            if (source == null) {
                return 0;
            }
            log.debug("尝试将字符串转换为整数: {}", source);
            
            // 如果是枚举字符串，先转换为枚举，再获取代码
            try {
                if ("DRAFT".equalsIgnoreCase(source)) {
                    return ArticleStatus.DRAFT.toCode();
                } else if ("PUBLISHED".equalsIgnoreCase(source)) {
                    return ArticleStatus.PUBLISHED.toCode();
                } else if ("OFFLINE".equalsIgnoreCase(source)) {
                    return ArticleStatus.OFFLINE.toCode();
                }
                
                // 尝试直接转换为整数
                return Integer.parseInt(source);
            } catch (NumberFormatException e) {
                log.warn("无法将字符串 '{}' 转换为整数，返回默认值0", source);
                return 0;
            }
        }
    }
    
    /**
     * ArticleStatus到字符串的转换器 - 统一存储为字符串枚举名称
     */
    @WritingConverter
    public static class ArticleStatusToStringConverter implements Converter<ArticleStatus, String> {
        @Override
        public String convert(ArticleStatus source) {
            if (source == null) {
                return null;
            }
            String result = source.getName();
            log.debug("将ArticleStatus转换为字符串: {}", result);
            return result;
        }
    }
}