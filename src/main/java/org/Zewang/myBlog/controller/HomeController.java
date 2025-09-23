package org.Zewang.myBlog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: controller
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/21 21:56
 */

@Controller // 表示这个类是一个控制器
@RequestMapping("/") // 表示这个控制器处理的请求路径，所有以/开头的请求都会由它处理
public class HomeController {
    @GetMapping // 表示这个方法处理GET请求
    public String index() {
        return "index"; // 返回模板名字，对应 templates/index.html
    }

}
