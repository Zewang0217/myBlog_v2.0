package org.Zewang.myBlog.controller;

import org.Zewang.myBlog.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: controller
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/21 21:56
 */

@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String index() {
        return "欢迎访问我的博客API服务！请使用前端应用来访问博客功能。";
    }

    @GetMapping("/test")
    public ApiResponse<String> test() {
        return ApiResponse.success("后端服务正常运行");
    }

}