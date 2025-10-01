package org.Zewang.myBlog.service.category.impl;


import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.dao.ArticleCategoryMapper;
import org.Zewang.myBlog.dao.CategoryMapper;
import org.Zewang.myBlog.model.Category;
import org.Zewang.myBlog.service.category.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 分类服务实现类
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:30
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final ArticleCategoryMapper articleCategoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.info("获取所有分类");
        try {
            return categoryMapper.findAll();
        } catch (Exception e) {
            log.error("获取所有分类失败", e);
            throw new BusinessException("获取所有分类失败" + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        log.info("获取分类 id={}", id);

        if (id <= 0 || id == null) {
            throw new BusinessException("无效的分类ID");
        }

        try {
            Category category = categoryMapper.findById(id);
            if (category == null) {
                log.warn("没找到ID为{}的分类", id);
                throw new BusinessException("没找到ID为" + id + "的分类");
            }
            log.info("分类创建成功，ID为{}", category.getId());
            return category;
        }catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取分类失败", e);
            throw new BusinessException("获取分类失败" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category updateCategory(Long id, Category category) {
        log.info("更新分类 id={}", id);

        if (id <= 0 || id == null) {
            throw new BusinessException("无效的分类ID");
        }

        if (category == null) {
            throw new BusinessException("无效的分类");
        }

        try {
            Category existingCategory = categoryMapper.findById(id);
            if (existingCategory == null) {
                log.warn("更新失败，分类不存在, ID: {}", id);
                throw new BusinessException("分类不存在");
            }

            if (!existingCategory.getName().equals(category.getName()) &&
                categoryMapper.existsByName(category.getName())) {
                log.warn("更新失败，名称已被其他分类使用: {}", category.getName());
                throw new BusinessException("分类名称已被其他分类使用");
            }

            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
            existingCategory.setUpdateTime(LocalDateTime.now().toString());

            int result = categoryMapper.update(existingCategory);
            if (result <= 0) {
                log.warn("更新分类失败, ID: {}", id);
                throw new BusinessException("更新分类失败");
            }

            log.info("分类更新成功, ID: {}", id);
            return existingCategory;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新分类失败", e);
            throw new BusinessException("更新分类失败" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category createCategory(Category category) {
        log.info("创建分类, 名称: {}", category.getName());

        if (category == null) {
            throw new IllegalArgumentException("分类信息不能为空");
        }

        if (categoryMapper.existsByName(category.getName())) {
            log.warn("创建分类失败，名称已存在: {}", category.getName());
            throw new BusinessException("分类名称已存在");
        }

        try {
            category.setCreateTime(LocalDateTime.now().toString())
                .setUpdateTime(LocalDateTime.now().toString());

            int result = categoryMapper.insert(category);
            if (result <= 0) {
                throw new BusinessException("创建分类失败");
            }

            log.info("分类创建成功, ID: {}", category.getId());
            return category;
        } catch (Exception e) {
            log.error("创建分类失败", e);
            throw new BusinessException("创建分类失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        log.info("删除分类, ID: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("无效的分类ID");
        }

        try {
            if (!categoryMapper.existsById(id)) {
                log.warn("删除失败，分类不存在, ID: {}", id);
                throw new BusinessException("分类不存在");
            }

            int result = categoryMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除分类失败");
            }

            log.info("分类删除成功, ID: {}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除分类失败, ID: " + id, e);
            throw new BusinessException("删除分类失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryMapper.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByArticleId(Long articleId) {
        if (articleId == null || articleId <= 0) {
            log.error("获取文章分类失败，文章ID无效");
            throw new BusinessException("文章ID无效");
        }
        try {
            return articleCategoryMapper.findCategoriesByArticleId(articleId);
        } catch (Exception e) {
            log.error("根据文章ID获取分类列表失败, articleId: {}", articleId, e);
            throw new BusinessException("获取分类列表失败: " + e.getMessage());
        }
    }
}