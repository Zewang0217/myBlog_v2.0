package org.Zewang.myBlog.service.search;

import org.Zewang.myBlog.dto.SearchArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.springframework.data.domain.Page;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 搜索服务接口
 * @email "Zewang0217@outlook.com"
 */
public interface SearchService {
    
    /**
     * 搜索文章
     * @param searchDTO 搜索条件
     * @return 分页后的文章列表
     */
    Page<Article> searchArticles(SearchArticleDTO searchDTO);
    
    /**
     * 快速搜索文章（只按关键词搜索）
     * @param keyword 搜索关键词
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return 分页后的文章列表
     */
    Page<Article> quickSearch(String keyword, Integer pageNum, Integer pageSize);
}