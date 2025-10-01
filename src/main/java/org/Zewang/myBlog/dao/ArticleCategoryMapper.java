package org.Zewang.myBlog.dao;


import java.util.List;
import org.Zewang.myBlog.dto.ArticleCategory;
import org.Zewang.myBlog.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章分类关联Mapper
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:21
 */

@Mapper
public interface ArticleCategoryMapper {

    List<ArticleCategory> findByArticleId(@Param("articleId") Long articleId);

    List<ArticleCategory> findByCategoryId(@Param("categoryId") Long categoryId);

    int insert(ArticleCategory articleCategory);

    int deleteByArticleId(@Param("articleId") Long articleId);

    int deleteByCategoryId(@Param("categoryId") Long categoryId);

    List<Long> findArticleIdsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);
    
    /**
     * 根据文章ID获取分类列表
     * @param articleId 文章ID
     * @return 分类列表
     */
    List<Category> findCategoriesByArticleId(@Param("articleId") Long articleId);
}