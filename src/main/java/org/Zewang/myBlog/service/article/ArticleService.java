package org.Zewang.myBlog.service.article;

import java.util.Set;
import org.Zewang.myBlog.dto.CreateArticleDTO;
import org.Zewang.myBlog.model.Article;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 文章服务接口 (MongoDB 版本)
 */
public interface ArticleService {
    /**
     * 获取所有文章
     */
    List<Article> getAllArticles();

    /**
     * 获取已发布文章
     */
    public List<Article> getPublishedArticles();
        /**
         * 通过分类ID获取文章
         */
    List<Article> getArticlesByCategoryIds(Set<String> categoryIds);

    /**
     * 根据ID获取文章
     */
    Optional<Article> getById(String id);

    /**
     * 创建文章
     */
    Article createArticle(CreateArticleDTO dto);

    /**
     * 更新文章
     */
    Article updateArticle(String id, CreateArticleDTO dto);

    /**
     * 删除文章
     */
    void deleteArticle(String id);

    /**
     * 发布文章
     */
    Article publishArticle(String id);

    /**
     * 检索文章
     */
    List<Article> searchArticles(String keyword);
    
    /**
     * 统计方法
     */
    long countTotalArticles();
    
    long countPublishedArticles();
    
    long countDraftArticles();
    
    List<Map<String, Object>> getArticleCategoryStats();

}
