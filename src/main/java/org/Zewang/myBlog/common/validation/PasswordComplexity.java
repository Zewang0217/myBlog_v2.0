package org.Zewang.myBlog.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 密码复杂度校验注解
 * 用于校验密码是否满足复杂度要求
 */
@Documented
@Constraint(validatedBy = PasswordComplexityValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordComplexity {
    String message() default "密码必须包含至少一个大写字母、一个小写字母和一个数字";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}