package org.Zewang.myBlog.dao;

import org.Zewang.myBlog.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ArticleMapper {
    /**
     * 查询所有文章
     */
    List<Article> findAll();

    /**
     * 根据ID查询文章
     */
    Article findById(@Param("id") Long id);

    /**
     * 新增文章
     */
    int insert(Article article);

    /**
     * 更新文章
     */
    int update(Article article);

    /**
     * 删除文章
     */
    int deleteById(@Param("id") Long id);

    /**
     * 检查文章是否存在
     */
    boolean existsById(@Param("id") Long id);

    /**
     * 根据标题查询文章是否存在
     */
    boolean existsByTitle(@Param("title") String title);

    /**
     * 根据标题查询文章是否存在（排除指定ID）
     */
    boolean existsByTitleAndIdNot(@Param("title") String title, @Param("id") Long id);
}