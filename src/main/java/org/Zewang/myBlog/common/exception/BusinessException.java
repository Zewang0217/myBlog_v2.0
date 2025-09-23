package org.Zewang.myBlog.common.exception;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: 业务异常类
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/23 21:28
 */

public class BusinessException  extends RuntimeException {
    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
