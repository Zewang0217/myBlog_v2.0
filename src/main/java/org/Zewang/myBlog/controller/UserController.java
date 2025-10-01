package org.Zewang.myBlog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

// Swagger注解
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 用户的controller层，负责用户管理 (MongoDB 版本)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/29 21:26
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@Slf4j
@Tag(name = "用户接口", description = "用户注册等相关接口")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "用户注册成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = User.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "注册失败，参数错误或用户已存在",
            content = @Content
        )
    })
    public ApiResponse<User> register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "用户注册信息",
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterDTO.class))
        )
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
