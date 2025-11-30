package org.Zewang.myBlog.repository;

import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @description: 文章 Repository
 * @author "Zewang"
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/01 21:26
 * @version 1.0
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    List<Article> findByStatus(ArticleStatus status);
    long countByStatus(ArticleStatus status);
    List<Article> findByAuthor(String author);
    Optional<Article> findByTitle(String title);
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, String id);
}
