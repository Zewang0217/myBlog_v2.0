package org.Zewang.myBlog.controller;


import lombok.extern.slf4j.Slf4j;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.dto.AuthResponseDTO;
import org.Zewang.myBlog.dto.LoginDTO;
import org.Zewang.myBlog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 登录认证流程
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/28 18:24
 */

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // 认证管理器

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) { // RequestBody作用：将json数据映射为对象

        // 在认证前添加日志
        log.debug("Attempting authentication for user: {}", loginDTO.getUsername());

        // 步骤一：验证用户名和密码
        Authentication authentication = authenticationManager.authenticate( // 调用Spring Security的认证机制验证用户凭据
            new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(),
                loginDTO.getPassword()
            )
        );

        log.debug("Authentication successful for user: {}", loginDTO.getUsername());

        // 步骤二：设置认证上下文
        SecurityContextHolder.getContext().setAuthentication(authentication); // 将认证信息保存到安全上下文中

        // 步骤三：获取用户详情并生成JWT令牌
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername()); // 获取用户详细信息
        log.debug("User details loaded: {}", userDetails.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        // 步骤四：返回包含令牌的响应
        return ApiResponse.success(new AuthResponseDTO(token));
    }

}
