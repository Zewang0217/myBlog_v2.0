package org.Zewang.myBlog.service.user.impl;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.Zewang.myBlog.model.User;
import org.Zewang.myBlog.repository.UserRepository;
import org.Zewang.myBlog.service.user.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 用户服务实现类 (MongoDB 版本)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/28 18:16
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initDefaultAdmin() {
        // 添加一个默认管理员（如果不存在）
        try {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
            }
        } catch (Exception e) {
            log.info("初始化默认管理员失败：" + e.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
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
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User createUser(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 保存用户
        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
        }
        return userRepository.save(user);
    }
}
