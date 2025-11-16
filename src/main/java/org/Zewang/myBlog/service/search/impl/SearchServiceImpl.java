package org.Zewang.myBlog.service.search.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.dto.SearchArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.service.search.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 搜索服务实现类
 * @email "Zewang0217@outlook.com"
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    
    private final MongoTemplate mongoTemplate;
    
    @Override
    public Page<Article> searchArticles(SearchArticleDTO searchDTO) {
        // 构建查询条件
        Criteria criteria = new Criteria();
        
        // 添加关键词搜索条件（标题或内容包含关键词）
        if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().trim().isEmpty()) {
            String keyword = searchDTO.getKeyword().trim();
            criteria.orOperator(
                where("title").regex(keyword, "i"), // 标题模糊匹配
                where("content").regex(keyword, "i")  // 内容模糊匹配
            );
        }
        
        // 添加分类过滤条件
        if (searchDTO.getCategoryId() != null && !searchDTO.getCategoryId().trim().isEmpty()) {
            criteria.and("categoryIds").in(searchDTO.getCategoryId());
        }
        
        // 添加作者过滤条件
        if (searchDTO.getAuthor() != null && !searchDTO.getAuthor().trim().isEmpty()) {
            criteria.and("author").is(searchDTO.getAuthor());
        }
        
        // 添加状态过滤条件（默认只查询已发布的文章）
        if (searchDTO.getStatus() != null) {
            criteria.and("status").is(searchDTO.getStatus());
        } else {
            criteria.and("status").is(1); // 默认为已发布状态
        }
        
        // 构建排序条件
        Sort sort = buildSort(searchDTO);
        
        // 构建分页查询
        PageRequest pageRequest = PageRequest.of(
            searchDTO.getPageNum() - 1, // MongoDB分页从0开始
            searchDTO.getPageSize(),
            sort
        );
        
        // 创建查询对象
        Query query = query(criteria).with(pageRequest);
        
        // 执行查询
        long total = mongoTemplate.count(query, Article.class);
        java.util.List<Article> articles = mongoTemplate.find(query, Article.class);
        
        return new PageImpl<>(articles, pageRequest, total);
    }
    
    @Override
    public Page<Article> quickSearch(String keyword, Integer pageNum, Integer pageSize) {
        // 构建简单的关键词搜索条件
        Criteria criteria = new Criteria();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            String trimmedKeyword = keyword.trim();
            criteria.orOperator(
                where("title").regex(trimmedKeyword, "i"),
                where("content").regex(trimmedKeyword, "i")
            );
        }
        
        // 默认只查询已发布的文章
        criteria.and("status").is(1);
        
        // 默认按创建时间降序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        
        PageRequest pageRequest = PageRequest.of(
            (pageNum != null && pageNum > 0) ? pageNum - 1 : 0,
            (pageSize != null && pageSize > 0 && pageSize <= 100) ? pageSize : 10,
            sort
        );
        
        Query query = query(criteria).with(pageRequest);
        
        long total = mongoTemplate.count(query, Article.class);
        java.util.List<Article> articles = mongoTemplate.find(query, Article.class);
        
        return new PageImpl<>(articles, pageRequest, total);
    }
    
    /**
     * 构建排序条件
     * @param searchDTO 搜索条件
     * @return 排序对象
     */
    private Sort buildSort(SearchArticleDTO searchDTO) {
        Sort.Direction direction = searchDTO.getSortDirection() == 1 ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        
        String sortField;
        switch (searchDTO.getSortBy()) {
            case 2:
                sortField = "viewCount"; // 按阅读量排序
                break;
            case 3:
                sortField = "likeCount"; // 按点赞数排序
                break;
            default:
                sortField = "createTime"; // 默认按时间排序
        }
        
        return Sort.by(direction, sortField);
    }
}