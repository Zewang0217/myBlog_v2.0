package org.Zewang.myBlog.common.exception;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 认证异常类，用于处理用户认证相关的错误
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/02
 */
public class AuthenticationException extends BaseException {
    private static final int DEFAULT_CODE = 401; // 默认状态码：Unauthorized

    /**
     * 构造方法
     * @param message 错误消息
     */
    public AuthenticationException(String message) {
        super(DEFAULT_CODE, message);
    }

    /**
     * 构造方法，自定义错误码
     * @param code 错误码
     * @param message 错误消息
     */
    public AuthenticationException(int code, String message) {
        super(code, message);
    }

    /**
     * 构造方法，包含异常原因
     * @param message 错误消息
     * @param cause 异常原因
     */
    public AuthenticationException(String message, Throwable cause) {
        super(DEFAULT_CODE, message, cause);
    }
}