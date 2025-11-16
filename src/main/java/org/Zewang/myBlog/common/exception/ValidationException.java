package org.Zewang.myBlog.common.exception;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 验证异常类，用于处理参数验证失败等场景
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/02
 */
public class ValidationException extends BaseException {
    private static final int DEFAULT_CODE = 400; // 默认状态码：Bad Request

    /**
     * 构造方法
     * @param message 错误消息
     */
    public ValidationException(String message) {
        super(DEFAULT_CODE, message);
    }

    /**
     * 构造方法，自定义错误码
     * @param code 错误码
     * @param message 错误消息
     */
    public ValidationException(int code, String message) {
        super(code, message);
    }

    /**
     * 构造方法，包含异常原因
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ValidationException(String message, Throwable cause) {
        super(DEFAULT_CODE, message, cause);
    }
}