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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章的控制器，处理以/article开头的请求
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/21 23:15
 */

/**
 * + @RequestBody：表示从 HTTP 请求的 body 中读取 JSON 数据
 * 并自动反序列化成 Java 对象 CreateArticleDTO
 *
 * + @Valid：表示要对这个对象进行 参数校验
 * 在 CreateArticleDTO 类中添加校验注解（非空等）
 *
 * + @PathVariable("id")
 * 从 URL 路径中提取动态参数，并绑定到方法参数上。
 */

@RestController
@RequestMapping("/api/article")// 修改为API路径
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class ArticleController {

    private final ArticleService articleService; // 注入ArticleService
    private final CategoryService categoryService;

    /**
     * 显示文章列表
     */
    @GetMapping("/list") // 表示这个方法处理GET请求
    @PreAuthorize("permitAll()") // 列表查看权限放宽到普通
    public ApiResponse<List<Article>> list() { // 返回文章列表
        List<Article> articles = articleService.getAllArticles();
        return ApiResponse.success(articles); // 静态方法可以直接调用
    }

    /**
     * 显示已发布的文章列表
     */
    @GetMapping("/published")
    @PreAuthorize("permitAll()") // 列表查看权限放宽到普通
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
    public ApiResponse<List<Article>> draftList() {
        List<Article> articles = articleService.getAllArticles().stream()
            .filter(article -> article.getStatus() == ArticleStatus.DRAFT)
            .collect(Collectors.toList());
        return ApiResponse.success(articles);
    }

    /**
     *  参数： @PathVariable("id")：从 URL 中提取 {id} 的值
     */

    // 获取文章详情
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // 文章查看权限放宽到普通
    public ApiResponse<Article> viewArticle(@PathVariable("id") Long id) {
        Article article = articleService.getById(id)
            .orElseThrow(() -> new RuntimeException("文章不存在或已经被删除"));
        return ApiResponse.success(article);

    }

    /**
     * 创建文章
     * @param dto 创建文章的参数
     * @return 创建的文章
     */
    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Article> createArticle(
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
    public ApiResponse<Article> publishArticle(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("无效的文章ID：" + id);
        }
        Article article = articleService.publishArticle(id);
        return ApiResponse.success(article);
    }

    // 修改文章
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Article> updateArticle(
        @PathVariable("id") Long id,
        @Valid @RequestBody CreateArticleDTO dto) {
        return ApiResponse.success(articleService.updateArticle(id, dto));
    }

    // 删除文章
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Article> deleteArticle(
        @PathVariable("id") Long id) {
        articleService.deleteArticle(id);
        return ApiResponse.success(null);
    }

    // 根据分类筛选文章
    @GetMapping("/listByCategories")
    @PreAuthorize("permitAll()")
    public ApiResponse<List<Article>> listByCategories(@RequestParam(required = false) String categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            // 如果没有提供分类ID，则返回空列表或所有已发布文章
            List<Article> articles = articleService.getAllArticles().stream()
                .filter(article -> article.getStatus() == ArticleStatus.PUBLISHED)
                .collect(Collectors.toList());
            return ApiResponse.success(articles);
        }

        // 将逗号分隔的字符串转换为Set<Long>
        Set<Long> categoryIdSet = Arrays.stream(categoryIds.split(","))
            .map(String::trim)
            .map(Long::parseLong)
            .collect(Collectors.toSet());

        List<Article> articles = articleService.getArticlesByCategoryIds(categoryIdSet);
        return ApiResponse.success(articles);
    }
}