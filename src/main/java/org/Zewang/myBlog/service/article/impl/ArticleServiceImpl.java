package org.Zewang.myBlog.service.article.impl;

import jakarta.persistence.SecondaryTable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.dao.ArticleCategoryMapper;
import org.Zewang.myBlog.dao.ArticleMapper;
import org.Zewang.myBlog.dto.ArticleCategory;
import org.Zewang.myBlog.dto.CreateArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.model.Category;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import org.Zewang.myBlog.service.article.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章服务实现类
 */
@Slf4j
@Service // 作用：Spring 的组件扫描注解，标识一个类为业务服务层组件
@RequiredArgsConstructor // 作用：Lombok 提供的注解，为类中所有 final 字段生成一个包含这些字段的构造函数
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;
    private final ArticleCategoryMapper articleCategoryMapper;

    @Override
    @Transactional(readOnly = true) // 声明式事务管理注解  readOnly = true：表示该事务是只读的，不会修改数据，可以优化数据库性能
    public List<Article> getAllArticles() {
        log.info("获取所有文章");
        try {
            List<Article> articles = articleMapper.findAll();
            // 为每篇文章填充分类信息
            for (Article article : articles) {
                try {
                    List<Category> categories = articleCategoryMapper.findCategoriesByArticleId(article.getId());
                    article.setCategories(categories);
                } catch (Exception e) {
                    log.warn("获取文章 {} 的分类信息失败: {}", article.getId(), e.getMessage());
                    // 即使获取分类信息失败，也不影响文章本身的返回
                    article.setCategories(new ArrayList<>());
                }
            }
            log.debug("成功获取 {} 篇文章", articles.size());
            return articles;
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
    @Transactional(readOnly = true)
    public List<Article> getArticlesByCategoryIds(Set<Long> categoryIds) {
        log.info("根据分类ID获取文章, categoryIds={}", categoryIds);

        if (categoryIds == null || categoryIds.isEmpty()) {
            log.warn("未指定分类ID，返回所有文章");
            return getAllArticles();
        }

        try {
            // 获取符合条件的文章ID
            List<Long> articleIds = articleCategoryMapper.findArticleIdsByCategoryIds(
                new ArrayList<>(categoryIds));

            // 获取所有文章并筛选，同时填充分类信息
            List<Article> allArticles = getAllArticles();
            return getAllArticles().stream()
                .filter(article -> articleIds.contains(article.getId()))
                .map(article -> {
                    // 确保文章包含分类信息
                    try {
                        List<Category> categories = articleCategoryMapper.findCategoriesByArticleId(article.getId());
                        article.setCategories(categories);
                    } catch (Exception e) {
                        log.warn("获取文章 {} 的分类信息失败: {}", article.getId(), e.getMessage());
                        // 即使获取分类信息失败，也不影响文章本身的返回
                        article.setCategories(new ArrayList<>());
                    }
                    return article;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据分类获取文章列表失败", e);
            throw new BusinessException("获取文章列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Article> getById(Long id) {
        log.info("根据ID获取文章, id={}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("无效的文章ID");
        }
        
        try {
            Article article = articleMapper.findById(id);
            if (article == null) {
                log.warn("未找到ID为 {} 的文章", id);
                return Optional.empty();
            }
            
            // 填充文章的分类信息
            try {
                List<Category> categories = articleCategoryMapper.findCategoriesByArticleId(id);
                article.setCategories(categories);
            } catch (Exception e) {
                log.warn("获取文章 {} 的分类信息失败: {}", id, e.getMessage());
                // 即使获取分类信息失败，也不影响文章本身的返回
                article.setCategories(new ArrayList<>());
            }
            
            return Optional.of(article);
        } catch (Exception e) {
            log.error("获取文章失败, id=" + id, e);
            throw new BusinessException("获取文章失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // Exception.class 表示发生此异常时进行回滚
    public Article createArticle(CreateArticleDTO dto) {
        log.info("创建文章, 标题: {}", dto.title());
        
        // 参数校验
        if (dto == null) {
            throw new IllegalArgumentException("文章信息不能为空");
        }
        
        // 检查标题是否已存在
        if (articleMapper.existsByTitle(dto.title())) {
            log.warn("创建文章失败，标题已存在: {}", dto.title());
            throw new BusinessException("文章标题已存在");
        }

        if (dto == null) {// 参数校验
            throw new IllegalArgumentException("文章信息不能为空");
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

            // 保存文章
            int result = articleMapper.insert(article);
            if (result <= 0) {
                throw new BusinessException("创建文章失败");
            }
            
            // 处理文章分类关联
            handleArticleCategories(article.getId(), dto.categoryIds());
            
            log.info("文章创建成功, ID: {}", article.getId());
            return article;
        } catch (Exception e) {
            log.error("创建文章失败", e);
            throw new BusinessException("创建文章失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Article updateArticle(Long id, CreateArticleDTO dto) {
        log.info("更新文章, ID: {}", id);
        
        // 参数校验
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("无效的文章ID");
        }
        if (dto == null) {
            throw new IllegalArgumentException("更新内容不能为空");
        }

        try {
            // 检查文章是否存在
            Article existingArticle = articleMapper.findById(id);

            if (existingArticle == null) {
                log.warn("更新失败，文章不存在, ID: {}", id);
                throw new BusinessException("文章不存在或已被删除");
            }

            // 检查标题是否已被其他文章使用
            if (!existingArticle.getTitle().equals(dto.title()) &&
                articleMapper.existsByTitleAndIdNot(dto.title(), id)) {
                log.warn("更新失败，标题已被其他文章使用: {}", dto.title());
                throw new BusinessException("文章标题已被其他文章使用");
            }

            // 更新文章信息
            existingArticle.setTitle(dto.title())
                .setContent(dto.content())
                .setAuthor(dto.author())
                .setUpdateTime(LocalDateTime.now());

            // 执行更新
            int result = articleMapper.update(existingArticle);
            if (result <= 0) {
                throw new BusinessException("更新文章失败");
            }
            
            // 处理文章分类关联
            handleArticleCategories(id, dto.categoryIds());
            
            log.info("文章更新成功, ID: {}", id);
            return existingArticle;
        } catch (BusinessException e) {
            throw e; // 业务异常直接抛出
        } catch (Exception e) {
            log.error("更新文章失败, ID: " + id, e);
            throw new BusinessException("更新文章失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Article publishArticle(Long id) {
        log.info("发布文章，ID：{}", id);

        // 参数校验
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("无效的文章ID");
        }

        try {
            // 检查文章是否存在
            Article existingArticle = articleMapper.findById(id);
            if (existingArticle == null) {
                log.warn("发布失败，文章不存在, ID: {}", id);
                throw new BusinessException("文章不存在或已被删除");
            }

            // 更新文章状态
            existingArticle.setStatus(ArticleStatus.PUBLISHED)
                .setUpdateTime(LocalDateTime.now());

            // 更新
            int result = articleMapper.update(existingArticle);
            if (result <= 0) {
                throw new BusinessException("发布文章失败");
            }

            log.info("发布文章成功，ID：{}");
            return existingArticle;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发布文章失败, ID: " + id, e);
            throw new BusinessException("发布文章失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        log.info("删除文章, ID: {}", id);
        
        // 参数校验
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("无效的文章ID");
        }

        try {
            // 检查文章是否存在
            if (!articleMapper.existsById(id)) {
                log.warn("删除失败，文章不存在, ID: {}", id);
                throw new BusinessException("文章不存在或已被删除");
            }

            // 删除文章分类关联
            articleCategoryMapper.deleteByArticleId(id);
            
            // 执行删除
            int result = articleMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除文章失败");
            }
            
            log.info("文章删除成功, ID: {}", id);
        } catch (BusinessException e) {
            throw e; // 业务异常直接抛出
        } catch (Exception e) {
            log.error("删除文章失败, ID: " + id, e);
            throw new BusinessException("删除文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理文章分类关联
     * 
     * 此方法负责管理文章与分类之间的多对多关联关系：
     * 1. 首先删除文章现有的所有分类关联
     * 2. 然后根据传入的分类ID列表，重新建立新的关联关系
     * 
     * @param articleId 文章ID
     * @param categoryIds 分类ID列表
     */
    private void handleArticleCategories(Long articleId, List<Long> categoryIds) {
        // 参数校验
        if (articleId == null || articleId <= 0) {
            throw new IllegalArgumentException("无效的文章ID");
        }
        
        try {
            // 先删除原有的分类关联，确保数据一致性
            articleCategoryMapper.deleteByArticleId(articleId);
            
            // 添加新的分类关联
            if (categoryIds != null && !categoryIds.isEmpty()) {
                for (Long categoryId : categoryIds) {
                    // 确保分类ID有效
                    if (categoryId != null && categoryId > 0) {
                        // 创建文章分类关联对象
                        ArticleCategory articleCategory = new ArticleCategory();
                        articleCategory.setArticleId(articleId);
                        articleCategory.setCategoryId(categoryId);
                        // 插入新的关联记录
                        articleCategoryMapper.insert(articleCategory);
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理文章分类关联失败, articleId: " + articleId, e);
            throw new BusinessException("处理文章分类关联失败: " + e.getMessage());
        }
    }
}