package org.Zewang.myBlog.common;

import org.Zewang.myBlog.common.exception.AuthenticationException;
import org.Zewang.myBlog.common.exception.BaseException;
import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.common.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 全局异常处理器，用于捕获和处理应用程序中的所有异常
 *               通过使用 @RestControllerAdvice 注解，这个类会自动拦截所有由 @RestController
 *               标记的控制器中抛出的异常，并进行统一处理
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/23 19:48
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理方法参数校验异常 - 增强版
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        // 获取字段错误映射，提供更详细的错误信息
        Map<String, String> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "字段验证失败"
                ));
        
        // 构建错误消息
        StringBuilder errorMsg = new StringBuilder();
        fieldErrors.forEach((field, message) -> {
            errorMsg.append(field).append(": ").append(message).append("; ");
        });
        
        // 移除末尾的分号和空格
        String message = errorMsg.toString();
        if (message.endsWith("; ")) {
            message = message.substring(0, message.length() - 2);
        }
        
        log.info("参数校验失败：{}", message);
        return ApiResponse.error(400, message);
    }

    /**
     * 处理自定义验证异常
     */
    @ExceptionHandler(ValidationException.class)
    public ApiResponse<?> handleCustomValidationException(ValidationException e) {
        log.info("自定义验证异常：{}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<?> handleAuthenticationException(AuthenticationException e) {
        log.info("认证异常：{}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        log.info("业务异常：{}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理基础异常（捕获所有自定义异常）
     */
    @ExceptionHandler(BaseException.class)
    public ApiResponse<?> handleBaseException(BaseException e) {
        log.info("自定义异常：{}，错误码：{}", e.getMessage(), e.getCode());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足：{}", e.getMessage());
        return ApiResponse.error(403, "权限不足，无法访问该资源");
    }

    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<?> handleMissingParamsException(MissingServletRequestParameterException e) {
        log.info("请求参数缺失：{}", e.getMessage());
        String message = String.format("缺少必要参数：%s", e.getParameterName());
        return ApiResponse.error(400, message);
    }

    /**
     * 处理请求参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<?> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info("参数类型不匹配：{}", e.getMessage());
        String message = String.format("参数'%s'类型错误，期望类型：%s", 
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        return ApiResponse.error(400, message);
    }

    /**
     * 处理请求体格式错误异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info("请求体格式错误：{}", e.getMessage());
        return ApiResponse.error(400, "请求体格式错误，请检查JSON格式是否正确");
    }

    /**
     * 处理 RuntimeException 异常
     * 当控制器中抛出 RuntimeException 或其子类异常时，会进入这个方法
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return ApiResponse.error(500, "服务器内部错误：" + e.getMessage());
    }

    /**
     * 处理其他所有未被特定处理器捕获的异常
     * 这是一个兜底的异常处理方法，处理所有其他类型的异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        log.error("未处理的异常", e);
        return ApiResponse.error(500, "服务器内部错误，请稍后重试");
    }


}
