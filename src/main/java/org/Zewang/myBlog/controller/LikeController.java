package org.Zewang.myBlog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.model.Like;
import org.Zewang.myBlog.service.like.LikeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
 * @description: 点赞管理接口
 * @email "Zewang0217@outlook.com"
 */
@RestController
@RequestMapping("api/likes")
@RequiredArgsConstructor
@Tag(name = "点赞接口", description = "文章点赞相关接口")
public class LikeController {
    
    private final LikeService likeService;
    
    // 创建点赞
    @PostMapping("/article/{articleId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "点赞文章", description = "为指定文章点赞（需要登录）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "点赞成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Like.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "请求参数错误或已点赞",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "未认证",
            content = @Content
        )
    })
    public ApiResponse<Like> createLike(
        @Parameter(description = "文章ID", required = true) @PathVariable String articleId,
        Authentication authentication) {
        
        String userId = authentication.getName();
        String username = authentication.getName();
        
        Like like = likeService.createLike(articleId, userId, username);
        return ApiResponse.success(like);
    }
    
    // 取消点赞
    @DeleteMapping("/article/{articleId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "取消点赞", description = "取消对指定文章的点赞（需要登录）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "取消点赞成功"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "请求参数错误或未点赞",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "未认证",
            content = @Content
        )
    })
    public ApiResponse<String> cancelLike(
        @Parameter(description = "文章ID", required = true) @PathVariable String articleId,
        Authentication authentication) {
        
        String userId = authentication.getName();
        likeService.cancelLike(articleId, userId);
        return ApiResponse.success("取消点赞成功");
    }
    
    // 获取点赞状态
    @GetMapping("/article/{articleId}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取点赞状态", description = "获取当前用户对指定文章的点赞状态（需要登录）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取点赞状态"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "未认证",
            content = @Content
        )
    })
    public ApiResponse<Map<String, Boolean>> getLikeStatus(
        @Parameter(description = "文章ID", required = true) @PathVariable String articleId,
        Authentication authentication) {
        
        String userId = authentication.getName();
        boolean isLiked = likeService.isLiked(articleId, userId);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("liked", isLiked);
        return ApiResponse.success(result);
    }
    
    // 获取文章点赞列表
    @GetMapping("/article/{articleId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取文章点赞列表", description = "获取指定文章的点赞列表")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取点赞列表",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Like.class))}
        )
    })
    public ApiResponse<List<Like>> getLikesByArticleId(
        @Parameter(description = "文章ID", required = true) @PathVariable String articleId) {
        
        List<Like> likes = likeService.getLikesByArticleId(articleId);
        return ApiResponse.success(likes);
    }
    
    // 获取用户点赞列表
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取我的点赞", description = "获取当前登录用户的点赞列表")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取点赞列表",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Like.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "未认证",
            content = @Content
        )
    })
    public ApiResponse<List<Like>> getMyLikes(Authentication authentication) {
        
        String userId = authentication.getName();
        List<Like> likes = likeService.getLikesByUserId(userId);
        return ApiResponse.success(likes);
    }
    
    // 统计文章点赞数
    @GetMapping("/article/{articleId}/count")
    @PreAuthorize("permitAll()")
    @Operation(summary = "统计文章点赞数", description = "获取指定文章的点赞数量")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取点赞数量"
        )
    })
    public ApiResponse<Map<String, Long>> countLikes(
        @Parameter(description = "文章ID", required = true) @PathVariable String articleId) {
        
        long count = likeService.countLikesByArticleId(articleId);
        
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ApiResponse.success(result);
    }
}