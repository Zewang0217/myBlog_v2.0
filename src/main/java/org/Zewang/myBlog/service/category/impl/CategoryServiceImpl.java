package org.Zewang.myBlog.service.category.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.dto.CreateCategoryDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.model.Category;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import org.Zewang.myBlog.repository.ArticleRepository;
import org.Zewang.myBlog.repository.CategoryRepository;
import org.Zewang.myBlog.service.category.CategoryService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 分类服务实现类 (MongoDB 版本)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:30
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public List<Category> getAllCategories() {
        log.info("获取所有分类");
        try {
            List<Category> categories = categoryRepository.findAll();

            // 统计每个分类的文章数量
            Map<String, Long> categoryArticleCounts = getCategoryArticleCounts();

            // 设置文章数量
            for (Category category : categories) {
                Long count = categoryArticleCounts.getOrDefault(category.getId(), 0L);
                category.setArticleCount(count.intValue());
            }

            return categories;
        } catch (Exception e) {
            log.error("获取所有分类失败", e);
            throw new BusinessException("获取所有分类失败" + e.getMessage());
        }
    }

    /**
     * 获取每个分类的文章数量统计
     */
    private Map<String, Long> getCategoryArticleCounts() {
        try {
            List<Article> allArticles = articleRepository.findAll();

            // 统计每个分类的文章数量（只统计已发布的文章）
            return allArticles.stream()
                .filter(article -> {
                    if (article.getStatus() == null) return false;

                    // 处理数字类型的状态
                    if (article.getStatus() instanceof ArticleStatus) {
                        return article.getStatus() == ArticleStatus.PUBLISHED;
                    }

                    // 处理字符串类型的状态
                    if (article.getStatus().getDescription() instanceof String) {
                        return "PUBLISHED".equalsIgnoreCase((String) article.getStatus().getDescription());
                    }

                    // 处理数字类型的状态
//                    if (article.getStatus().getCode() instanceof Number) {
//                        return ((Number) article.getStatus().getCode()).intValue() == 1;
//                    }

                    return false;
                })
                .flatMap(article -> article.getCategories() != null ?
                    article.getCategories().stream() : java.util.stream.Stream.empty())
                .collect(Collectors.groupingBy(Category::getId, Collectors.counting()));
    } catch (Exception e) {
            log.error("统计分类文章数量失败", e);
            return java.util.Collections.emptyMap();
    }
}

@Override
public Category getCategoryById(String id) {
    log.info("获取分类 id={}", id);

    if (id == null || id.isEmpty()) {
            throw new BusinessException("无效的分类ID");
    }

    try {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("没找到ID为" + id + "的分类"));
    } catch (BusinessException e) {
        throw e;
    } catch (Exception e) {
        log.error("获取分类失败", e);
        throw new BusinessException("获取分类失败" + e.getMessage());
    }
}

@Override
public Category updateCategory(String id, CreateCategoryDTO dto) {
    log.info("更新分类 id={}", id);

    if (id == null || id.isEmpty()) {
        throw new BusinessException("无效的分类ID");
    }

    if (dto == null) {
        throw new BusinessException("无效的分类数据");
    }

    try {
        Category existingCategory = categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("分类不存在"));

        if (!existingCategory.getName().equals(dto.name()) &&
            categoryRepository.existsByName(dto.name())) {
            log.warn("更新失败，名称已被其他分类使用: {}", dto.name());
            throw new BusinessException("分类名称已被其他分类使用");
        }

        existingCategory.setName(dto.name());
        existingCategory.setDescription(dto.description());
        existingCategory.setUpdateTime(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(existingCategory);

        log.info("分类更新成功, ID: {}", id);
        return updatedCategory;
    } catch (BusinessException e) {
        throw e;
    } catch (Exception e) {
        log.error("更新分类失败", e);
        throw new BusinessException("更新分类失败" + e.getMessage());
    }
}

@Override
public Category createCategory(CreateCategoryDTO dto) {
    log.info("创建分类, 名称: {}", dto.name());

    if (dto == null) {
        throw new IllegalArgumentException("分类信息不能为空");
    }

    if (categoryRepository.existsByName(dto.name())) {
        log.warn("创建分类失败，名称已存在: {}", dto.name());
        throw new BusinessException("分类名称已存在");
    }

    try {
        Category category = new Category();
        category.setName(dto.name());
        category.setDescription(dto.description());
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(category);

        log.info("分类创建成功, ID: {}", savedCategory.getId());
        return savedCategory;
    } catch (Exception e) {
        log.error("创建分类失败", e);
        throw new BusinessException("创建分类失败: " + e.getMessage());
    }
}

@Override
public void deleteCategory(String id) {
    log.info("删除分类, ID: {}", id);

    if (id == null || id.isEmpty()) {
        throw new IllegalArgumentException("无效的分类ID");
    }

    try {
        if (!categoryRepository.existsById(id)) {
            log.warn("删除失败，分类不存在, ID: {}", id);
            throw new BusinessException("分类不存在");
        }

        categoryRepository.deleteById(id);

        log.info("分类删除成功, ID: {}", id);
    } catch (BusinessException e) {
        throw e;
    } catch (Exception e) {
        log.error("删除分类失败, ID: " + id, e);
        throw new BusinessException("删除分类失败: " + e.getMessage());
    }
}

@Override
public boolean existsByName(String name) {
    return categoryRepository.existsByName(name);
}
}
