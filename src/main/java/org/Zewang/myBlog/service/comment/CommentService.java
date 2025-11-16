package org.Zewang.myBlog.service.comment;

import java.util.List;
import org.Zewang.myBlog.dto.CreateCommentDTO;
import org.Zewang.myBlog.model.Comment;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 评论服务接口
 * @email "Zewang0217@outlook.com"
 */
public interface CommentService {
    
    /**
     * 创建评论
     * @param dto 评论数据
     * @param userId 用户ID
     * @param username 用户名
     * @return 创建的评论
     */
    Comment createComment(CreateCommentDTO dto, String userId, String username);
    
    /**
     * 根据文章ID查询评论列表
     * @param articleId 文章ID
     * @return 评论列表
     */
    List<Comment> getCommentsByArticleId(String articleId);
    
    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 当前用户ID
     */
    void deleteComment(String id, String userId);
    
    /**
     * 根据用户ID查询评论列表
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> getCommentsByUserId(String userId);
    
    /**
     * 统计文章的评论数
     * @param articleId 文章ID
     * @return 评论数量
     */
    long countCommentsByArticleId(String articleId);
}