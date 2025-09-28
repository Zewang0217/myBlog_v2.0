package org.Zewang.myBlog.model;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 用户认证的数据传输层
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/28 17:57
 */

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String role; // / 可以是 ROLE_ADMIN, ROLE_USER 等
}
