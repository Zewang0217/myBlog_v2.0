package org.Zewang.myBlog.service.user.impl;


import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.Zewang.myBlog.dao.UserMapper;
import org.Zewang.myBlog.model.User;
import org.Zewang.myBlog.service.user.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 用户服务实现类
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/28 18:16
 */

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private AtomicLong idGenerator = new AtomicLong(2); // 从2开始，因为已经有一个管理员了

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;

    }

    @PostConstruct // 此注解作用是：表示在类初始化完成后执行，通常用于初始化数据
    @Transactional // 此注解作用是：表示方法在事务内执行，如果方法执行过程中抛出异常，则事务会被回滚
    public void initDefaultAdmin() {
        // 添加一个默认管理员（如果不存在）
        try {
            if (!userMapper.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                userMapper.insert(admin);
            }
        } catch (Exception e) {
            log.info("初始化默认管理员失败：" + e.getMessage());
        }
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
        return userMapper.findByUsername(username);
    }

    @Override
    public User createUser(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 保存用户
        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
        }
        userMapper.insert(user);
        return user;
    }
}
