package org.Zewang.myBlog.service.user.impl;


import java.util.ArrayList;
import java.util.List;
import org.Zewang.myBlog.model.User;
import org.Zewang.myBlog.service.user.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 用户服务实现类
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/28 18:16
 */

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    // 模拟用户数据存储
    private List<User> users = new ArrayList<>();

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        // 添加一个默认管理员用户
        User admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        // 密码应该是加密后的 “admin123”
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ROLE_ADMIN");
        users.add(admin);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username)  throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().replace("ROLE_", ""))
            .build();
    }

    @Override
    public User findByUsername(String username) {
        return users.stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
}
