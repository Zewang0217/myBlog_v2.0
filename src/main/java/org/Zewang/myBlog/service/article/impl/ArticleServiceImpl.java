package org.Zewang.myBlog.service.article.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.dto.CreateArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.model.Category;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import org.Zewang.myBlog.repository.ArticleRepository;
import org.Zewang.myBlog.repository.CategoryRepository;
import org.Zewang.myBlog.service.article.ArticleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章服务实现类 (MongoDB 版本)
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Cacheable(value = "articles", key = "'all'")
    public List<Article> getAllArticles() {
        log.info("获取所有文章");
        try {
            return articleRepository.findAll();
        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            String message = e.getMessage();
            if (message == null) {
                message = "未知错误";
            }
            throw new BusinessException("获取文章列表失败: " + message);
        }
    }

    @Override
    @Cacheable(value = "articles", key = "'published'")
    public List<Article> getPublishedArticles() {
        log.info("获取已发布的文章");
        try {
            return articleRepository.findByStatus(ArticleStatus.PUBLISHED);
        } catch (Exception e) {
            log.error("获取已发布文章列表失败", e);
            throw new BusinessException("获取已发布文章列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "articles", key = "'byCategoryIds:' + #categoryIds")
    public List<Article> getArticlesByCategoryIds(Set<String> categoryIds) {
        log.info("根据分类ID获取文章, categoryIds={}", categoryIds);

        if (categoryIds == null || categoryIds.isEmpty()) {
            log.warn("未指定分类ID，返回所有文章");
            return getAllArticles();
        }

        try {
            // 获取所有文章并筛选
            List<Article> allArticles = getAllArticles();
            return allArticles.stream()
                .filter(article -> article.getCategories() != null &&
                    article.getCategories().stream()
                        .anyMatch(category -> categoryIds.contains(category.getId())))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据分类获取文章列表失败", e);
            throw new BusinessException("获取文章列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "articles", key = "'byId:' + #id")
    public Optional<Article> getById(String id) {
        log.info("根据ID获取文章, id={}", id);

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("无效的文章ID");
        }

        try {
            return articleRepository.findById(id);
        } catch (Exception e) {
            log.error("获取文章失败, id=" + id, e);
            throw new BusinessException("获取文章失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "articles", allEntries = true)
    public Article createArticle(CreateArticleDTO dto) {
        log.info("创建文章, 标题: {}", dto.title());

        // 参数校验
        if (dto == null) {
            throw new IllegalArgumentException("文章信息不能为空");
        }

        // 检查标题是否已存在
        if (articleRepository.existsByTitle(dto.title())) {
            log.warn("创建文章失败，标题已存在: {}", dto.title());
            throw new BusinessException("文章标题已存在");
        }

        try {
            // 构建文章对象
            Article article = new Article()
                .setTitle(dto.title())
                .setContent(dto.content())
                .setAuthor(dto.author())
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setStatus(dto.status() != null ? dto.status() : ArticleStatus.DRAFT);  // 默认草稿状态

            // 处理文章分类关联
            if (dto.categoryIds() != null && !dto.categoryIds().isEmpty()) {
                List<Category> categories = categoryRepository.findAllById(dto.categoryIds());
                article.setCategories(categories);
            }

            // 保存文章
            Article savedArticle = articleRepository.save(article);

            log.info("文章创建成功, ID: {}", savedArticle.getId());
            return savedArticle;
        } catch (Exception e) {
            log.error("创建文章失败", e);
            throw new BusinessException("创建文章失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "articles", allEntries = true)
    public Article updateArticle(String id, CreateArticleDTO dto) {
        log.info("更新文章, ID: {}", id);

        // 参数校验
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("无效的文章ID");
        }
        if (dto == null) {
            throw new IllegalArgumentException("更新内容不能为空");
        }

        try {
            // 检查文章是否存在
            Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文章不存在或已被删除"));

            // 检查标题是否已被其他文章使用
            if (!existingArticle.getTitle().equals(dto.title()) &&
                articleRepository.existsByTitleAndIdNot(dto.title(), id)) {
                log.warn("更新失败，标题已被其他文章使用: {}", dto.title());
                throw new BusinessException("文章标题已被其他文章使用");
            }

            // 更新文章信息
            existingArticle.setTitle(dto.title())
                .setContent(dto.content())
                .setAuthor(dto.author())
                .setUpdateTime(LocalDateTime.now());

            // 处理文章分类关联
            if (dto.categoryIds() != null) {
                List<Category> categories = categoryRepository.findAllById(dto.categoryIds());
                existingArticle.setCategories(categories);
            }

            // 执行更新
            Article updatedArticle = articleRepository.save(existingArticle);

            log.info("文章更新成功, ID: {}", id);
            return updatedArticle;
        } catch (BusinessException e) {
            throw e; // 业务异常直接抛出
        } catch (Exception e) {
            log.error("更新文章失败, ID: " + id, e);
            throw new BusinessException("更新文章失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "articles", allEntries = true)
    public Article publishArticle(String id) {
        log.info("发布文章，ID：{}", id);

        // 参数校验
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("无效的文章ID");
        }

        try {
            // 检查文章是否存在
            Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文章不存在或已被删除"));

            // 更新文章状态
            existingArticle.setStatus(ArticleStatus.PUBLISHED)
                .setUpdateTime(LocalDateTime.now());

            // 更新
            Article updatedArticle = articleRepository.save(existingArticle);

            log.info("发布文章成功，ID：{}", id);
            return updatedArticle;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发布文章失败, ID: " + id, e);
            throw new BusinessException("发布文章失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "articles", allEntries = true)
    public void deleteArticle(String id) {
        log.info("删除文章, ID: {}", id);

        // 参数校验
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("无效的文章ID");
        }

        try {
            // 检查文章是否存在
            if (!articleRepository.existsById(id)) {
                log.warn("删除失败，文章不存在, ID: {}", id);
                throw new BusinessException("文章不存在或已被删除");
            }

            // 执行删除
            articleRepository.deleteById(id);

            log.info("文章删除成功, ID: {}", id);
        } catch (BusinessException e) {
            throw e; // 业务异常直接抛出
        } catch (Exception e) {
            log.error("删除文章失败, ID: " + id, e);
            throw new BusinessException("删除文章失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "articles", key = "'search_' + #keyword")
    public List<Article> searchArticles(String keyword) {
        log.info("搜索文章，关键词：{}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllArticles();
        }

        try {
            List<Article> allArticles = getAllArticles();
            String lowerCaseKeyword = keyword.toLowerCase().trim();

            return allArticles.stream()
                .filter(article ->
                    (article.getTitle() != null && article.getTitle().toLowerCase().contains(lowerCaseKeyword)) ||
                        (article.getContent() != null && article.getContent().toLowerCase().contains(lowerCaseKeyword))
                ).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("搜索文章失败, 关键词: " + keyword + ", 错误: " + e.getMessage());
            throw new BusinessException("搜索文章失败: " + e.getMessage());
        }
    }
}
