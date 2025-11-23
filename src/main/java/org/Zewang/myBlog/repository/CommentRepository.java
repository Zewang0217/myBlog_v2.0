package org.Zewang.myBlog.repository;

import java.util.List;
import org.Zewang.myBlog.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 评论数据访问层
 * @email "Zewang0217@outlook.com"
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    /**
     * 根据文章ID查询评论列表
     * @param articleId 文章ID
     * @return 评论列表
     */
    List<Comment> findByArticleIdOrderByCreateTimeDesc(String articleId);

    /**
     * 根据文章ID和状态查询评论列表
     * @param articleId 文章ID
     * @param status 评论状态：0-正常，1-已删除
     * @return 评论列表
     */
    List<Comment> findByArticleIdAndStatusOrderByCreateTimeDesc(String articleId, Integer status);
    
    /**
     * 根据文章ID和父评论ID查询评论列表
     * @param articleId 文章ID
     * @param parentId 父评论ID
     * @return 评论列表
     */
    List<Comment> findByArticleIdAndParentIdOrderByCreateTimeAsc(String articleId, String parentId);
    
    /**
     * 根据用户ID查询评论列表
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> findByUserIdOrderByCreateTimeDesc(String userId);
    
    /**
     * 统计文章的评论数
     * @param articleId 文章ID
     * @return 评论数量
     */
    long countByArticleIdAndStatus(String articleId, Integer status);
}