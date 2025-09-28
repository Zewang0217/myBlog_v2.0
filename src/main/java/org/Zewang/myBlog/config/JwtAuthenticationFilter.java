// src/main/java/org/Zewang/myBlog/config/JwtAuthenticationFilter.java
package org.Zewang.myBlog.config;

// JWT 认证过滤器
// 作用：
//     ·拦截每个HTTP请求
//     ·从请求头中提取JWT令牌
//     ·验证JWT令牌有效性
//     ·构建Spring Security认证对象

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.Zewang.myBlog.util.JwtUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component // 将该类注册为Spring Bean
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // 延迟初始化，避免循环依赖
    private final ObjectProvider<UserDetailsService> userDetailsService;

    public JwtAuthenticationFilter(ObjectProvider<UserDetailsService> userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // 检查请求头中是否含有Bearer令牌

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7); // 提取令牌
            String username = null;

            try {
                username = jwtUtil.extractUsername(jwt); // 获取用户名
            } catch (Exception e) {
                // JWT解析异常
            }

            // 如果用户名存在且当前没有认证信息
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.getObject().loadUserByUsername(username);

                // 验证令牌有效性
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // 创建认证令牌并设置到安全上下文中
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response); // 继续处理请求
    }
}
