package org.Zewang.myBlog.service.article.impl;


import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.dao.ArticleMapper;
import org.Zewang.myBlog.dto.CreateArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.service.article.ArticleService;
import org.springframework.stereotype.Service;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章类接口实现
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/21 23:09
 */

@Service
@RequiredArgsConstructor // 创建一个构造函数，用于自动注入ArticleMapper对象
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;

    @Override
    public List<Article> getAllArticles() {
        return articleMapper.findAll();
    }

    @Override
    public Optional<Article> getById(Long id) {
        return Optional.ofNullable(articleMapper.findById(id)); //  如果文章不存在，MyBatis 返回 null
    }

    @Override
    public void createArticle(CreateArticleDTO dto) {
        Article article = new Article();

        article.setTitle(dto.title());
        article.setContent(dto.content());
        article.setAuthor(dto.author());
        article.setCreatedAt(java.time.LocalDateTime.now());
        article.setUpdatedAt(java.time.LocalDateTime.now());

        articleMapper.insert(article);
    }

    @Override
    public void updateArticle(Long id, CreateArticleDTO dto) {
        Article article = articleMapper.findById(id);
        if (article != null) {
            article.setTitle(dto.title());
            article.setContent(dto.content());
            article.setAuthor(dto.author());
            article.setUpdatedAt(java.time.LocalDateTime.now());

            articleMapper.update(article);
        }
    }

    @Override
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
    }
}
