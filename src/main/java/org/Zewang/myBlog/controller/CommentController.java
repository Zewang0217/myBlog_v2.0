package org.Zewang.myBlog.controller;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.dto.CreateCommentDTO;
import org.Zewang.myBlog.model.Comment;
import org.Zewang.myBlog.service.comment.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
 * @description: 评论管理接口
 * @email "Zewang0217@outlook.com"
 */
@RestController
@RequestMapping("api/comments")
@RequiredArgsConstructor
@Tag(name = "评论接口", description = "文章评论相关接口")
public class CommentController {
    
    private final CommentService commentService;
    
    // 创建评论
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建评论", description = "创建新的文章评论（需要登录）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "评论创建成功",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Comment.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "请求参数错误",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "未认证",
            content = @Content
        )
    })
    public ApiResponse<Comment> createComment(
        @Valid @RequestBody CreateCommentDTO dto,
        Authentication authentication) {
        
        // 从认证信息中获取用户ID和用户名
        // 这里简化处理，实际应该从UserDetails中获取
        String userId = authentication.getName(); // 假设用户名就是用户ID
        String username = authentication.getName();
        
        Comment comment = commentService.createComment(dto, userId, username);
        return ApiResponse.success(comment);
    }
    
    // 根据文章ID查询评论列表
    @GetMapping("/article/{articleId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取文章评论", description = "获取指定文章的评论列表")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取评论列表",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Comment.class))}
        )
    })
    public ApiResponse<List<Comment>> getCommentsByArticleId(
        @Parameter(description = "文章ID", required = true) @PathVariable String articleId) {
        
        List<Comment> comments = commentService.getCommentsByArticleId(articleId);
        return ApiResponse.success(comments);
    }
    
    // 删除评论
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除评论", description = "删除指定的评论（只有评论作者或管理员可操作）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "评论删除成功"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "请求参数错误",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "未认证",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "权限不足",
            content = @Content
        )
    })
    public ApiResponse<String> deleteComment(
        @Parameter(description = "评论ID", required = true) @PathVariable String id,
        Authentication authentication) {
        
        String userId = authentication.getName();
        commentService.deleteComment(id, userId);
        return ApiResponse.success("评论删除成功");
    }
    
    // 获取当前用户的评论列表
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取我的评论", description = "获取当前登录用户的评论列表")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "成功获取评论列表",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Comment.class))}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "未认证",
            content = @Content
        )
    })
    public ApiResponse<List<Comment>> getMyComments(Authentication authentication) {
        
        String userId = authentication.getName();
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        return ApiResponse.success(comments);
    }
}