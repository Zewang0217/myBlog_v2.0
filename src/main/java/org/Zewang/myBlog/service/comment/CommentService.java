package org.Zewang.myBlog.service.comment;

import java.util.List;
import org.Zewang.myBlog.dto.CreateCommentDTO;
import org.Zewang.myBlog.model.Comment;

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
    
    /**
     * 统计总评论数
     * @return 总评论数量
     */
    long countTotalComments();
    
    /**
     * 统计待审核评论数
     * @return 待审核评论数量
     */
    long countPendingComments();
    
    /**
     * 获取最近的评论
     * @param limit 限制数量
     * @return 最近评论列表
     */
    List<Comment> getRecentComments(int limit);
    
    /**
     * 获取所有评论列表
     * @return 所有评论列表
     */
    List<Comment> getAllComments();
    
    /**
     * 根据ID获取评论
     * @param id 评论ID
     * @return 评论对象
     */
    Comment getCommentById(String id);
    
    /**
     * 更新评论状态
     * @param id 评论ID
     * @param status 评论状态：0-正常，1-已删除
     * @return 更新后的评论对象
     */
    Comment updateCommentStatus(String id, Integer status);
}