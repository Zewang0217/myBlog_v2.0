package org.Zewang.myBlog.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.dto.RegisterDTO;
import org.Zewang.myBlog.model.User;
import org.Zewang.myBlog.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 用户的controller层，负责用户管理
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/29 21:26
 */

@RestController // 此注解作用是：表示该类是一个控制器，可以处理 HTTP 请求，并返回 HTTP 响应，默认情况下，控制器方法返回的视图名称和控制器方法名称一致，例如：@GetMapping("/index")，则返回的视图名称为 index
@RequestMapping("/api/user")
@RequiredArgsConstructor // 此注解作用是：表示自动注入类中的所有字段，包括私有字段和受保护的字段
@PreAuthorize("permitAll()")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<User> register(
        @Valid @RequestBody RegisterDTO registerDTO
    ) {
        // 验证密码是否相同
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            log.info("用户" + registerDTO.getUsername() + "注册失败，密码不一致");
            return ApiResponse.error(400, "两次输入的密码不一致");
        }

        // 检查用户是否存在
        if (userService.findByUsername(registerDTO.getUsername()) != null) {
            log.info("用户" + registerDTO.getUsername() + "注册失败，用户已存在");
            return ApiResponse.error(400, "用户已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());

        user.setRole("ROLE_USER");

        User savedUser = userService.createUser(user);

        savedUser.setPassword(null); // 不返回密码信息

        log.info("用户" + registerDTO.getUsername() + "注册成功");
        return ApiResponse.success(savedUser);
    }

}
