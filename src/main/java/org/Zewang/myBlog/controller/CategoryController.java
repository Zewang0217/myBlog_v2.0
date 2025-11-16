package org.Zewang.myBlog.controller;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.dto.CreateCategoryDTO;
import org.Zewang.myBlog.model.Category;
import org.Zewang.myBlog.service.category.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Swagger注解
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章分类管理接口 (MongoDB 版本)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/30 21:58
 */
@RestController
@RequestMapping("api/category")
@RequiredArgsConstructor
@Tag(name = "分类接口", description = "文章分类管理相关接口")
public class CategoryController {

    private final CategoryService categoryService;

    // 获取所有分类 - 允许所有人访问
    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取所有分类", description = "获取系统中所有文章分类")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取分类列表",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Category.class))}
        )
    })
    public ApiResponse<List<Category>> list() {
        List<Category> categories = categoryService.getAllCategories();
        return ApiResponse.success(categories);
    }

    // 创建分类 - 仅管理员可访问
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建分类", description = "创建新的文章分类（仅管理员可访问）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "分类创建成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Category.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "请求参数错误",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "权限不足",
            content = @Content
        )
    })
    public ApiResponse<Category> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "分类信息",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateCategoryDTO.class))
        )
        @Valid @RequestBody CreateCategoryDTO dto) {
        Category createdCategory = categoryService.createCategory(dto);
        return ApiResponse.success(createdCategory);
    }

    // 更新分类 - 仅管理员可访问
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新分类", description = "更新已存在的分类信息（仅管理员可访问）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "分类更新成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Category.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "请求参数错误",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "权限不足",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "分类不存在",
            content = @Content
        )
    })
    public ApiResponse<Category> update(
        @Parameter(description = "分类ID", required = true) @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "分类信息",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateCategoryDTO.class))
        )
        @Valid @RequestBody CreateCategoryDTO dto) {
        Category updatedCategory = categoryService.updateCategory(id, dto);
        return ApiResponse.success(updatedCategory);
    }

    // 删除分类 - 仅管理员可访问
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除分类", description = "删除指定分类（仅管理员可访问）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "分类删除成功"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "分类ID无效",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "权限不足",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "分类不存在",
            content = @Content
        )
    })
    public ApiResponse<Void> delete(
        @Parameter(description = "分类ID") @PathVariable("id") String id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null);
    }
}
