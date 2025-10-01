package org.Zewang.myBlog.controller.admin;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.dto.CreateArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.model.Category;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import org.Zewang.myBlog.service.article.ArticleService;
import org.Zewang.myBlog.service.category.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Swagger注解
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章的控制器，处理以/article开头的请求 (MongoDB 版本)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/21 23:15
 */
@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@Tag(name = "文章接口", description = "文章管理相关接口")
public class ArticleController {

    private final ArticleService articleService;
    private final CategoryService categoryService;

    /**
     * 显示文章列表
     */
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取所有文章", description = "获取系统中的所有文章列表")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取文章列表",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Article.class))}
        )
    })
    public ApiResponse<List<Article>> list() {
        List<Article> articles = articleService.getAllArticles();
        return ApiResponse.success(articles);
    }

    /**
     * 显示已发布的文章列表
     */
    @GetMapping("/published")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取已发布的文章", description = "获取所有已发布的文章列表")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取已发布文章列表",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Article.class))}
        )
    })
    public ApiResponse<List<Article>> publishedList() {
        List<Article> articles = articleService.getAllArticles().stream()
            .filter(article -> article.getStatus() == ArticleStatus.PUBLISHED)
            .collect(Collectors.toList());
        return ApiResponse.success(articles);
    }

    /**
     * 显示草稿列表
     */
    @GetMapping("/drafts")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "获取草稿文章", description = "获取所有草稿文章列表（仅管理员可访问）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取草稿文章列表",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Article.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "权限不足",
            content = @Content
        )
    })
    public ApiResponse<List<Article>> draftList() {
        List<Article> articles = articleService.getAllArticles().stream()
            .filter(article -> article.getStatus() == ArticleStatus.DRAFT)
            .collect(Collectors.toList());
        return ApiResponse.success(articles);
    }

    // 获取文章详情
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取文章详情", description = "根据文章ID获取文章详细信息")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取文章详情",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Article.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "文章不存在",
            content = @Content
        )
    })
    public ApiResponse<Article> viewArticle(
        @Parameter(description = "文章ID") @PathVariable("id") String id) {
        Article article = articleService.getById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在"));
        return ApiResponse.success(article);
    }

    /**
     * 创建文章
     * @param dto 创建文章的参数
     * @return 创建的文章
     */
    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "创建文章", description = "创建新文章（仅管理员可访问）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "文章创建成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Article.class))}
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
    public ApiResponse<Article> createArticle(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "文章信息",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateArticleDTO.class))
        )
        @Valid @RequestBody CreateArticleDTO dto) {
        Article article = articleService.createArticle(dto);
        return ApiResponse.success(article);
    }

    /**
     * 发布文章
     * @param id 文章的ID
     * @return 发布的文章
     */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "发布文章", description = "将草稿文章发布为正式文章（仅管理员可访问）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "文章发布成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Article.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "文章ID无效",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "权限不足",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "文章不存在",
            content = @Content
        )
    })
    public ApiResponse<Article> publishArticle(
        @Parameter(description = "文章ID") @PathVariable("id") String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("无效的文章ID：" + id);
        }
        Article article = articleService.publishArticle(id);
        return ApiResponse.success(article);
    }

    // 修改文章
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "更新文章", description = "更新已存在的文章（仅管理员可访问）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "文章更新成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Article.class))}
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
            description = "文章不存在",
            content = @Content
        )
    })
    public ApiResponse<Article> updateArticle(
        @Parameter(description = "文章ID") @PathVariable("id") String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "更新的文章信息",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateArticleDTO.class))
        )
        @Valid @RequestBody CreateArticleDTO dto) {
        return ApiResponse.success(articleService.updateArticle(id, dto));
    }

    // 删除文章
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "删除文章", description = "删除指定文章（仅管理员可访问）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "文章删除成功"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "文章ID无效",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "权限不足",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "文章不存在",
            content = @Content
        )
    })
    public ApiResponse<Article> deleteArticle(
        @Parameter(description = "文章ID") @PathVariable("id") String id) {
        articleService.deleteArticle(id);
        return ApiResponse.success(null);
    }

    // 根据分类筛选文章
    @GetMapping("/listByCategories")
    @PreAuthorize("permitAll()")
    @Operation(summary = "根据分类筛选文章", description = "根据一个或多个分类ID筛选文章")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取筛选后的文章列表",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Article.class))}
        )
    })
    public ApiResponse<List<Article>> listByCategories(
        @Parameter(description = "分类ID列表，用逗号分隔")
        @RequestParam(required = false) String categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            // 如果没有提供分类ID，则返回空列表或所有已发布文章
            List<Article> articles = articleService.getAllArticles().stream()
                .filter(article -> article.getStatus() == ArticleStatus.PUBLISHED)
                .collect(Collectors.toList());
            return ApiResponse.success(articles);
        }

        // 将逗号分隔的字符串转换为Set<String>
        Set<String> categoryIdSet = Arrays.stream(categoryIds.split(","))
            .map(String::trim)
            .collect(Collectors.toSet());

        List<Article> articles = articleService.getArticlesByCategoryIds(categoryIdSet);
        return ApiResponse.success(articles);
    }
}
