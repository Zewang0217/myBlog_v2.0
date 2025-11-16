package org.Zewang.myBlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 用户登录数据传输对象
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/28 17:58
 */
@Schema(description = "用户登录数据传输对象")
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 18, message = "用户名长度必须在1-18个字符之间")
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度必须在6-30个字符之间")
    @Schema(description = "密码", example = "123456")
    private String password;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
