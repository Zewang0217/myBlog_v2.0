package org.Zewang.myBlog.service.comment.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.dto.CreateCommentDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.model.Comment;
import org.Zewang.myBlog.repository.CommentRepository;
import org.Zewang.myBlog.repository.ArticleRepository;
import org.Zewang.myBlog.service.comment.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 评论服务实现类
 * @email "Zewang0217@outlook.com"
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    
    @Override
    @Transactional
    public Comment createComment(CreateCommentDTO dto, String userId, String username) {
        logger.info("创建评论，文章ID：{}, 用户ID：{}", dto.getArticleId(), userId);
        
        // 检查文章是否存在
        if (!articleRepository.existsById(dto.getArticleId())) {
            throw new BusinessException("文章不存在");
        }
        
        // 如果是回复评论，检查父评论是否存在
        if (dto.getParentId() != null) {
            Comment parentComment = commentRepository.findById(dto.getParentId())
                .orElseThrow(() -> new BusinessException("父评论不存在"));
            
            // 检查父评论是否属于同一篇文章
            if (!parentComment.getArticleId().equals(dto.getArticleId())) {
                throw new BusinessException("父评论不属于该文章");
            }
        }
        
        // 创建评论
        Comment comment = new Comment();
        comment.setArticleId(dto.getArticleId());
        comment.setUserId(userId);
        comment.setUsername(username);
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId());
        comment.setStatus(0); // 0表示正常状态
        comment.setLikes(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        
        Comment savedComment = commentRepository.save(comment);
        
        // 更新文章评论数
        Article article = articleRepository.findById(dto.getArticleId()).orElse(null);
        if (article != null) {
            article.setCommentCount(article.getCommentCount() + 1);
            articleRepository.save(article);
        }
        
        return savedComment;
    }
    
    @Override
    public List<Comment> getCommentsByArticleId(String articleId) {
        logger.info("查询文章评论，文章ID：{}", articleId);
        return commentRepository.findByArticleIdOrderByCreateTimeDesc(articleId);
    }
    
    @Override
    @Transactional
    public void deleteComment(String id, String userId) {
        logger.info("删除评论，评论ID：{}, 用户ID：{}", id, userId);
        
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new BusinessException("评论不存在"));
        
        // 检查权限：只有评论作者或管理员可以删除
        // 这里简化处理，实际应该检查用户角色
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("没有权限删除该评论");
        }
        
        // 软删除：更新状态为已删除
        comment.setStatus(1);
        comment.setUpdateTime(LocalDateTime.now());
        commentRepository.save(comment);
        
        // 更新文章评论数
        Article article = articleRepository.findById(comment.getArticleId()).orElse(null);
        if (article != null && article.getCommentCount() > 0) {
            article.setCommentCount(article.getCommentCount() - 1);
            articleRepository.save(article);
        }
    }
    
    @Override
    public List<Comment> getCommentsByUserId(String userId) {
        logger.info("查询用户评论，用户ID：{}", userId);
        return commentRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    @Override
    public long countCommentsByArticleId(String articleId) {
        logger.info("统计文章评论数，文章ID：{}", articleId);
        return commentRepository.countByArticleIdAndStatus(articleId, 0);
    }
}