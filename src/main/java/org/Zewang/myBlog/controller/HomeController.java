package org.Zewang.myBlog.controller;

import org.Zewang.myBlog.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
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
 * @description: 首页控制器
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/21 21:56
 */

@RestController
@RequestMapping("/")
@Tag(name = "首页接口", description = "系统首页相关接口")
public class HomeController {

    @GetMapping
    @Operation(summary = "首页", description = "返回系统首页欢迎信息")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功返回首页信息"
        )
    })
    public String index() {
        return "欢迎访问我的博客API服务！请使用前端应用来访问博客功能。";
    }

    @GetMapping("/test")
    @Operation(summary = "测试接口", description = "用于测试后端服务是否正常运行")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "服务正常运行",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))}
        )
    })
    public ApiResponse<String> test() {
        return ApiResponse.success("后端服务正常运行");
    }
}
