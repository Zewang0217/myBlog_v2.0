package org.Zewang.myBlog.repository;

import java.util.List;
import org.Zewang.myBlog.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 点赞仓库接口
 * @email "Zewang0217@outlook.com"
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, String> {
    
    /**
     * 根据用户ID和文章ID查询点赞记录
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 点赞记录
     */
    Like findByUserIdAndArticleId(String userId, String articleId);
    
    /**
     * 根据文章ID查询点赞列表
     * @param articleId 文章ID
     * @return 点赞列表
     */
    List<Like> findByArticleId(String articleId);
    
    /**
     * 根据用户ID查询点赞列表
     * @param userId 用户ID
     * @return 点赞列表
     */
    List<Like> findByUserId(String userId);
    
    /**
     * 根据文章ID统计点赞数
     * @param articleId 文章ID
     * @return 点赞数量
     */
    long countByArticleId(String articleId);
    
    /**
     * 删除用户对文章的点赞
     * @param userId 用户ID
     * @param articleId 文章ID
     */
    void deleteByUserIdAndArticleId(String userId, String articleId);
}