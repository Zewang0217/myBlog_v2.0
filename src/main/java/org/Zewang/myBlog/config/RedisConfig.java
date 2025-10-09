// src/main/java/org/Zewang/myBlog/config/RedisConfig.java
package org.Zewang.myBlog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis配置类
 * 负责配置Redis连接、序列化方式和缓存管理
 *
 * @EnableCaching 启用Spring的缓存支持，允许在方法上使用@Cacheable等注解
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 创建并配置ObjectMapper实例
     * ObjectMapper是Jackson库的核心类，用于Java对象与JSON之间的转换
     *
     * @return 配置好的ObjectMapper实例
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 注册JavaTimeModule模块，支持Java 8日期时间API（LocalDate, LocalDateTime等）的序列化
        mapper.registerModule(new JavaTimeModule());

        // 启用默认类型信息，在序列化时添加类型信息，便于反序列化时识别具体类型
        // NON_FINAL表示对所有非final类型的类都添加类型信息
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(), // 多态类型验证器，确保类型安全
            ObjectMapper.DefaultTyping.NON_FINAL  // 为非final类型添加类型信息
        );

        // 禁用将日期序列化为时间戳，使用ISO-8601格式（如"2025-10-01T12:00:00"）
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }

    /**
     * 配置RedisTemplate Bean
     * RedisTemplate是Spring Data Redis的核心类，用于执行Redis操作
     *
     * @param connectionFactory Redis连接工厂，由Spring Boot自动配置
     * @return 配置好的RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 设置Redis连接工厂
        template.setConnectionFactory(connectionFactory);

        // 创建配置好的ObjectMapper
        ObjectMapper mapper = createObjectMapper();

        // 创建JSON序列化器，使用GenericJackson2JsonRedisSerializer会自动添加类型信息
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        // 设置Key的序列化器 - 使用StringRedisSerializer，确保key为可读的字符串格式
        template.setKeySerializer(new StringRedisSerializer());
        // 设置Value的序列化器 - 使用JSON格式序列化，便于跨语言使用和人工阅读
        template.setValueSerializer(serializer);
        // 设置Hash Key的序列化器 - 同样使用字符串序列化
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置Hash Value的序列化器 - 使用JSON格式序列化
        template.setHashValueSerializer(serializer);

        // 初始化模板，确保所有属性设置完成
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置缓存管理器Bean
     * CacheManager是Spring缓存抽象的核心接口，用于管理各种缓存
     *
     * @param redisConnectionFactory Redis连接工厂
     * @return 配置好的RedisCacheManager实例
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 创建配置好的ObjectMapper
        ObjectMapper mapper = createObjectMapper();
        // 创建JSON序列化器
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        // 创建Redis缓存配置
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            // 设置缓存条目的生存时间(TTL)为1小时，超过时间自动过期
            .entryTtl(Duration.ofHours(1))
            // 设置key的序列化方式为字符串序列化
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            // 设置value的序列化方式为JSON序列化
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            // 禁止缓存null值，避免缓存穿透问题
            .disableCachingNullValues();

        // 构建Redis缓存管理器
        return RedisCacheManager.builder(redisConnectionFactory)
            // 设置默认缓存配置
            .cacheDefaults(cacheConfiguration)
            .build();
    }
}