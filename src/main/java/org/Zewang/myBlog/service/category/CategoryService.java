package org.Zewang.myBlog.service.category;


import java.util.List;
import org.Zewang.myBlog.model.Category;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 分类服务层
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:29
 */

public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);

    boolean existsByName(String name);
    
    /**
     * 根据文章ID获取分类列表
     * @param articleId 文章ID
     * @return 分类列表
     */
    List<Category> getCategoriesByArticleId(Long articleId);
}