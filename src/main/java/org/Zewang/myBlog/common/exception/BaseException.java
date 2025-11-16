package org.Zewang.myBlog.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 异常基类，所有自定义异常都应继承此类
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/02
 */
public abstract class BaseException extends RuntimeException {
    private int code;          // 错误码
    private String message;    // 错误消息

    /**
     * 构造方法
     * @param code 错误码
     * @param message 错误消息
     */
    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法，包含异常原因
     * @param code 错误码
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
    
    // Getters and Setters
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}