package org.Zewang.myBlog.dto;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 认证响应数据
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/28 17:59
 */

@Data
public class AuthResponseDTO {
    private String token; // 令牌
    private String tokenType = "Bearer "; // 默认令牌前缀

    public AuthResponseDTO(String token) { // 构造函数
        this.token = token;
    }

}
