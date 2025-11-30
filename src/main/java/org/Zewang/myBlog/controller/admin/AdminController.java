package org.Zewang.myBlog.controller.admin;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.model.User;
import org.Zewang.myBlog.service.article.ArticleService;
import org.Zewang.myBlog.service.comment.CommentService;
import org.Zewang.myBlog.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import org.Zewang.myBlog.model.Comment;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 管理员控制器，负责管理和统计功能
 * @email "Zewang0217@outlook.com"
 * @date 2025/11/30
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard/stats")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        stats.put("totalUsers", userService.countTotalUsers());
        stats.put("activeUsers", userService.countActiveUsers());
        stats.put("userRegistrationTrend", userService.getUserRegistrationTrend());
        
        // 文章统计
        stats.put("totalArticles", articleService.countTotalArticles());
        stats.put("publishedArticles", articleService.countPublishedArticles());
        stats.put("draftArticles", articleService.countDraftArticles());
        stats.put("articleCategoryStats", articleService.getArticleCategoryStats());
        
        // 评论统计
        stats.put("totalComments", commentService.countTotalComments());
        stats.put("pendingComments", commentService.countPendingComments());
        
        return ApiResponse.success(stats);
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // 清除密码信息
        users.forEach(user -> user.setPassword(null));
        return ApiResponse.success(users);
    }
    
    /**
     * 获取单个用户详情
     */
    @GetMapping("/users/{id}")
    public ApiResponse<User> getUserById(@PathVariable String id) {
        User user = userService.findById(id);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        // 清除密码信息
        user.setPassword(null);
        return ApiResponse.success(user);
    }
    
    /**
     * 创建新用户
     */
    @PostMapping("/users")
    public ApiResponse<User> createUser(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userService.findByUsername(user.getUsername()) != null) {
            return ApiResponse.error(400, "用户名已存在");
        }
        
        User createdUser = userService.createUser(user);
        // 清除密码信息
        createdUser.setPassword(null);
        return ApiResponse.success(createdUser);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/users/{id}")
    public ApiResponse<User> updateUser(@PathVariable String id, @RequestBody User user) {
        // 检查用户是否存在
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        // 检查用户名是否已被其他用户使用
        User userWithSameUsername = userService.findByUsername(user.getUsername());
        if (userWithSameUsername != null && !userWithSameUsername.getId().equals(id)) {
            return ApiResponse.error(400, "用户名已存在");
        }
        
        // 更新用户信息
        user.setId(id);
        // 如果密码为空，保持原密码不变
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }
        
        User updatedUser = userService.updateUser(user);
        // 清除密码信息
        updatedUser.setPassword(null);
        return ApiResponse.success(updatedUser);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        // 检查用户是否存在
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        // 不允许删除管理员用户
        if ("ROLE_ADMIN".equals(existingUser.getRole())) {
            return ApiResponse.error(400, "不允许删除管理员用户");
        }
        
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }
    
    /**
     * 获取最近活动
     */
    @GetMapping("/dashboard/recent-activities")
    public ApiResponse<List<Map<String, Object>>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // 获取最近的评论
        List<Comment> recentComments = commentService.getRecentComments(10);
        for (Comment comment : recentComments) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "comment");
            activity.put("content", comment.getContent());
            activity.put("username", comment.getUsername());
            activity.put("createTime", comment.getCreateTime());
            activity.put("articleId", comment.getArticleId());
            activities.add(activity);
        }
        
        // 可以添加更多类型的活动，比如文章发布、用户注册等
        
        // 按时间倒序排序
        activities.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("createTime");
            LocalDateTime timeB = (LocalDateTime) b.get("createTime");
            return timeB.compareTo(timeA);
        });
        
        return ApiResponse.success(activities);
    }
    
    /**
     * 获取所有评论列表
     */
    @GetMapping("/comments")
    public ApiResponse<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return ApiResponse.success(comments);
    }
    
    /**
     * 获取单个评论详情
     */
    @GetMapping("/comments/{id}")
    public ApiResponse<Comment> getCommentById(@PathVariable String id) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return ApiResponse.error(404, "评论不存在");
        }
        return ApiResponse.success(comment);
    }
    
    /**
     * 更新评论状态
     */
    @PutMapping("/comments/{id}/status")
    public ApiResponse<Comment> updateCommentStatus(@PathVariable String id, @RequestBody Map<String, Integer> statusMap) {
        Integer status = statusMap.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return ApiResponse.error(400, "无效的评论状态，只能是0（正常）或1（已删除）");
        }
        
        Comment updatedComment = commentService.updateCommentStatus(id, status);
        if (updatedComment == null) {
            return ApiResponse.error(404, "评论不存在");
        }
        return ApiResponse.success(updatedComment);
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id, "admin"); // 管理员删除，使用固定用户ID
        return ApiResponse.success(null);
    }
}
