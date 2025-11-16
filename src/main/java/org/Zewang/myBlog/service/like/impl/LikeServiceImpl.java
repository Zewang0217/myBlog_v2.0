package org.Zewang.myBlog.service.like.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.model.Like;
import org.Zewang.myBlog.repository.ArticleRepository;
import org.Zewang.myBlog.repository.LikeRepository;
import org.Zewang.myBlog.service.like.LikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 点赞服务实现类
 * @email "Zewang0217@outlook.com"
 */
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    
    private final LikeRepository likeRepository;
    private final ArticleRepository articleRepository;
    
    @Override
    @Transactional
    public Like createLike(String articleId, String userId, String username) {
        // 检查文章是否存在
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new BusinessException("文章不存在"));
        
        // 检查是否已经点赞
        Like existingLike = likeRepository.findByUserIdAndArticleId(userId, articleId);
        if (existingLike != null) {
            throw new BusinessException("已经点赞过该文章");
        }
        
        // 创建点赞记录
        Like like = new Like();
        like.setUserId(userId);
        like.setUsername(username);
        like.setArticleId(articleId);
        like.setCreateTime(LocalDateTime.now());
        like.setUpdateTime(LocalDateTime.now());
        
        Like savedLike = likeRepository.save(like);
        
        // 更新文章点赞数
        article.setLikeCount(article.getLikeCount() + 1);
        articleRepository.save(article);
        
        return savedLike;
    }
    
    @Override
    @Transactional
    public void cancelLike(String articleId, String userId) {
        // 检查点赞记录是否存在
        Like like = likeRepository.findByUserIdAndArticleId(userId, articleId);
        if (like == null) {
            throw new BusinessException("未点赞该文章");
        }
        
        // 删除点赞记录
        likeRepository.deleteByUserIdAndArticleId(userId, articleId);
        
        // 更新文章点赞数
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article != null && article.getLikeCount() > 0) {
            article.setLikeCount(article.getLikeCount() - 1);
            articleRepository.save(article);
        }
    }
    
    @Override
    public boolean isLiked(String articleId, String userId) {
        Like like = likeRepository.findByUserIdAndArticleId(userId, articleId);
        return like != null;
    }
    
    @Override
    public List<Like> getLikesByArticleId(String articleId) {
        return likeRepository.findByArticleId(articleId);
    }
    
    @Override
    public List<Like> getLikesByUserId(String userId) {
        return likeRepository.findByUserId(userId);
    }
    
    @Override
    public long countLikesByArticleId(String articleId) {
        return likeRepository.countByArticleId(articleId);
    }
}