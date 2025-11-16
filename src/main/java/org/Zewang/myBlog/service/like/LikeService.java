package org.Zewang.myBlog.service.like;

import java.util.List;
import org.Zewang.myBlog.model.Like;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 点赞服务接口
 * @email "Zewang0217@outlook.com"
 */
public interface LikeService {
    
    /**
     * 创建点赞
     * @param articleId 文章ID
     * @param userId 用户ID
     * @param username 用户名
     * @return 点赞信息
     */
    Like createLike(String articleId, String userId, String username);
    
    /**
     * 取消点赞
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void cancelLike(String articleId, String userId);
    
    /**
     * 获取用户对文章的点赞状态
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    boolean isLiked(String articleId, String userId);
    
    /**
     * 根据文章ID获取点赞列表
     * @param articleId 文章ID
     * @return 点赞列表
     */
    List<Like> getLikesByArticleId(String articleId);
    
    /**
     * 根据用户ID获取点赞列表
     * @param userId 用户ID
     * @return 点赞列表
     */
    List<Like> getLikesByUserId(String userId);
    
    /**
     * 统计文章点赞数
     * @param articleId 文章ID
     * @return 点赞数量
     */
    long countLikesByArticleId(String articleId);
}