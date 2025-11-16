package org.Zewang.myBlog.common;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 是一个 通用的 API 响应封装类，它的作用是：
 *               统一后端返回给前端的数据格式，让前端知道：请求是否成功、哪里出错了、以及真正的数据在哪里。
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/23 19:29
 */

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = 200;
        response.message = "请求成功";
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = code;
        response.message = message;
        return response;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = 500;
        response.message = message;
        return response;
    }
}
