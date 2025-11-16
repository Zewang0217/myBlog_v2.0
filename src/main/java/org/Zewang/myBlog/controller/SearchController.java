package org.Zewang.myBlog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.dto.SearchArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.service.search.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Swagger注解
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// 使用完整包路径避免命名冲突
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 搜索管理接口
 * @email "Zewang0217@outlook.com"
 */
@RestController
@RequestMapping("api/search")
@RequiredArgsConstructor
@Tag(name = "搜索接口", description = "文章搜索相关接口")
public class SearchController {
    
    private final SearchService searchService;
    
    // 高级搜索文章
    @GetMapping("/articles")
    @PreAuthorize("permitAll()")
    @Operation(summary = "高级搜索文章", description = "根据多种条件搜索文章")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "搜索成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Page.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "请求参数错误",
            content = @Content
        )
    })
    public ApiResponse<Page<Article>> searchArticles(@Valid SearchArticleDTO searchDTO) {
        
        Page<Article> articles = searchService.searchArticles(searchDTO);
        return ApiResponse.success(articles);
    }
    
    // 快速搜索文章
    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "快速搜索文章", description = "根据关键词快速搜索文章")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "搜索成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Page.class))}
        )
    })
    public ApiResponse<Page<Article>> quickSearch(
        @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
        @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
        @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<Article> articles = searchService.quickSearch(keyword, pageNum, pageSize);
        return ApiResponse.success(articles);
    }
}