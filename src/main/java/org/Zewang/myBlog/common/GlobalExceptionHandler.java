package org.Zewang.myBlog.common;


import lombok.extern.slf4j.Slf4j;
import org.Zewang.myBlog.common.exception.BusinessException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 全局异常处理器，用于捕获和处理应用程序中的所有异常
 *               通过使用 @RestControllerAdvice 注解，这个类会自动拦截所有由 @RestController
 *               标记的控制器中抛出的异常，并进行统一处理
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/23 19:48
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理方法参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            errorMsg.append(error.getDefaultMessage()).append("; ");
        }
        // 移除末尾的分号和空格
        String message = errorMsg.toString();
        if (message.endsWith("; ")) {
            message = message.substring(0, message.length() - 2);
        }
        log.info("参数校验失败：{}", message);
        return ApiResponse.error(400, message);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        // 返回业务异常的错误信息
        log.info("业务异常：{}", e.getMessage());
        return ApiResponse.error(400, e.getMessage());
    }

    /**
     * 处理 RuntimeException 异常
     * 当控制器中抛出 RuntimeException 或其子类异常时，会进入这个方法
     *
     * @param e 捕获到的异常对象
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Void> handleRuntimeException(RuntimeException e) {
        // 将异常信息封装成统一的错误响应格式
        log.error("服务器内部错误", e);
        return ApiResponse.error(500, e.getMessage());
    }

    /**
     * 处理其他所有未被特定处理器捕获的异常
     * 这是一个兜底的异常处理方法，处理所有其他类型的异常
     *
     * @param e 捕获到的异常对象
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
//        返回通用的服务器内部错误信息
        log.error("服务器内部错误", e);
        return ApiResponse.error(500, e.getMessage());
    }


}
