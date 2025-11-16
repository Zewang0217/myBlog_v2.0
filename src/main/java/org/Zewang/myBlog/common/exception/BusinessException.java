package org.Zewang.myBlog.common.exception;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 业务异常类
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/23 21:28
 */
public class BusinessException extends BaseException {
    private static final int DEFAULT_CODE = 500; // 默认状态码：Internal Server Error

    /**
     * 构造方法
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(DEFAULT_CODE, message);
    }

    /**
     * 构造方法，自定义错误码
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(code, message);
    }

    /**
     * 构造方法，包含异常原因
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(DEFAULT_CODE, message, cause);
    }
}
