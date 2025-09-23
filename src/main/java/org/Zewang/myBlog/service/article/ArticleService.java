package org.Zewang.myBlog.service.article;

import org.Zewang.myBlog.dto.CreateArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 文章服务接口
 */
public interface ArticleService {
    /**
     * 获取所有文章（分页）
     */
    List<Article> getAllArticles();

    /**
     * 根据ID获取文章
     */
    Optional<Article> getById(Long id);

    /**
     * 创建文章
     */
    Article createArticle(CreateArticleDTO dto);

    /**
     * 更新文章
     */
    Article updateArticle(Long id, CreateArticleDTO dto);

    /**
     * 删除文章
     */
    void deleteArticle(Long id);

}