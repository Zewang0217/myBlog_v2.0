package org.Zewang.myBlog.repository;

import org.Zewang.myBlog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @description: 分类 Repository
 * @author "Zewang"
 * @version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
}
