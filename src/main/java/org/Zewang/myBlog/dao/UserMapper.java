package org.Zewang.myBlog.dao;


import org.Zewang.myBlog.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 用户数据访问接口
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/29 21:50
 */

@Mapper
public interface UserMapper {

    // 根据用户名查询用户
    User findByUsername(@Param("username") String username);

    // 插入新用户
    int insert(User user);

    // 检查用户是否存在
    boolean existsByUsername(@Param("username") String username);
}
