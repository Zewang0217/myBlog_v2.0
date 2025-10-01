package org.Zewang.myBlog.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.model.Category;
import org.Zewang.myBlog.service.category.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:58
 */

@RestController
@RequestMapping("api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 获取所有分类 - 允许所有人访问
    @GetMapping
    @PreAuthorize("permitAll()")
    public ApiResponse<List<Category>> list() {
        List<Category> categories = categoryService.getAllCategories();
        return ApiResponse.success(categories);
    }

    // 创建分类 - 仅管理员可访问
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Category> create(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ApiResponse.success(createdCategory);
    }

    // 更新分类 - 仅管理员可访问
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Category> update(@PathVariable("id") Long id, @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ApiResponse.success(updatedCategory);
    }

    // 删除分类 - 仅管理员可访问
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null);
    }
}

