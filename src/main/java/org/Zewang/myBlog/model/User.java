package org.Zewang.myBlog.model;


import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户角色")
    private String role; // / 可以是 ROLE_ADMIN, ROLE_USER 等
}
