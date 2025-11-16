package org.Zewang.myBlog.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 密码复杂度校验器实现
 * 校验密码是否包含至少一个大写字母、一个小写字母和一个数字
 */
public class PasswordComplexityValidator implements ConstraintValidator<PasswordComplexity, String> {
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return true; // 空值由@NotBlank等注解处理
        }
        
        // 检查是否包含至少一个大写字母
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        // 检查是否包含至少一个小写字母
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        // 检查是否包含至少一个数字
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        
        return hasUpperCase && hasLowerCase && hasDigit;
    }
}