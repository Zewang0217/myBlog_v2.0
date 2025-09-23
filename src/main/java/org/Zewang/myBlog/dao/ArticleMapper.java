package org.Zewang.myBlog.dao;

import java.util.List;

import org.Zewang.myBlog.model.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * DAO层，文章的数据访问接口
 */

@Mapper // 表示这个类是一个MyBatis的Mapper类
public interface ArticleMapper {
    // 查询所有文章
    List<Article> findAll();
    Article findById(Long id);
    int insert(Article article);
    int update(Article article);
    int deleteById(Long id);
}


