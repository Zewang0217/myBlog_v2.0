package org.Zewang.myBlog.dao;

import java.util.List;
import org.Zewang.myBlog.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CategoryMapper {

    List<Category> findAll();

    Category findById(@Param("id") Long id);

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("id") Long id);

    boolean existsById(@Param("id") Long id);

    boolean existsByName(@Param("name") String name);
}
