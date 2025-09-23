package org.Zewang.myBlog.service.article;

import java.util.List;
import java.util.Optional;
import org.Zewang.myBlog.dto.CreateArticleDTO;
import org.Zewang.myBlog.model.Article;

/**
 * 文章服务接口
 */

public interface ArticleService {
    List<Article> getAllArticles();

    Optional<Article> getById(Long id);

    void createArticle(CreateArticleDTO dto);

    void updateArticle(Long id, CreateArticleDTO dto);

    void deleteArticle(Long id);
}
