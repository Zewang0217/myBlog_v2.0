package org.Zewang.myBlog.service.category;

import java.util.List;
import org.Zewang.myBlog.model.Category;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 分类服务层 (MongoDB 版本)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:29
 */
public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(String id);

    Category createCategory(Category category);

    Category updateCategory(String id, Category category);

    void deleteCategory(String id);

    boolean existsByName(String name);
}
