package org.Zewang.myBlog.service.category.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.model.Category;
import org.Zewang.myBlog.repository.CategoryRepository;
import org.Zewang.myBlog.service.category.CategoryService;
import org.springframework.stereotype.Service;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 分类服务实现类 (MongoDB 版本)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        log.info("获取所有分类");
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            log.error("获取所有分类失败", e);
            throw new BusinessException("获取所有分类失败" + e.getMessage());
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
    public Category updateCategory(String id, Category category) {
        log.info("更新分类 id={}", id);

        if (id == null || id.isEmpty()) {
            throw new BusinessException("无效的分类ID");
        }

        if (category == null) {
            throw new BusinessException("无效的分类");
        }

        try {
            Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));

            if (!existingCategory.getName().equals(category.getName()) &&
                categoryRepository.existsByName(category.getName())) {
                log.warn("更新失败，名称已被其他分类使用: {}", category.getName());
                throw new BusinessException("分类名称已被其他分类使用");
            }

            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
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
    public Category createCategory(Category category) {
        log.info("创建分类, 名称: {}", category.getName());

        if (category == null) {
            throw new IllegalArgumentException("分类信息不能为空");
        }

        if (categoryRepository.existsByName(category.getName())) {
            log.warn("创建分类失败，名称已存在: {}", category.getName());
            throw new BusinessException("分类名称已存在");
        }

        try {
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
