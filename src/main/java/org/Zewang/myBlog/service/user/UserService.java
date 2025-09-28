package org.Zewang.myBlog.service.user;

import org.Zewang.myBlog.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService { // 继承UserDetailsService, 用于获取用户信息
    User findByUsername(String username);
}
