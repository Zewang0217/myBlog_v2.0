package org.Zewang.myBlog.dao;

import java.util.List;
import org.Zewang.myBlog.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleMapper {

    List<Article> findAll();

    Article findById(@Param("id") Long id);

    int insert(Article article);

    int update(Article article);

    int deleteById(@Param("id") Long id);

    boolean existsById(@Param("id") Long id);

    boolean existsByTitle(@Param("title") String title);

    boolean existsByTitleAndIdNot(@Param("title") String title, @Param("id") Long id);
    
    /**
     * 根据分类ID获取文章列表
     * @param categoryId 分类ID
     * @return 文章列表
     */
    List<Article> findByCategoryId(@Param("categoryId") Long categoryId);
}