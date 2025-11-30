package org.Zewang.myBlog.service.user;

import org.Zewang.myBlog.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService { // 继承UserDetailsService, 用于获取用户信息
    User findByUsername(String username);
    
    User findById(String id);

    User createUser(User user);
    
    // 统计方法
    long countTotalUsers();
    
    long countActiveUsers();
    
    List<Map<String, Object>> getUserRegistrationTrend();
    
    List<User> getAllUsers();
    
    User updateUser(User user);
    
    void deleteUser(String userId);
}
