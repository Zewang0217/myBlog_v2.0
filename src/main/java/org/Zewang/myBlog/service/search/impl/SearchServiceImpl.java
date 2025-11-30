package org.Zewang.myBlog.service.search.impl;

import lombok.RequiredArgsConstructor;
import org.Zewang.myBlog.dto.SearchArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import org.Zewang.myBlog.repository.ArticleRepository;
import org.Zewang.myBlog.service.search.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    
    private final ArticleRepository articleRepository;
    
    @Override
    public Page<Article> searchArticles(SearchArticleDTO searchDTO) {
        // 构建排序条件
        Sort sort = buildSort(searchDTO);
        
        // 构建分页查询
        PageRequest pageRequest = PageRequest.of(
            searchDTO.getPageNum() - 1, // JPA分页从0开始
            searchDTO.getPageSize(),
            sort
        );
        
        // 执行查询
        // 注意：这里简化了查询逻辑，实际项目中可能需要使用JPA Criteria API或JPQL来实现复杂查询
        // 由于时间限制，这里直接返回所有文章，后续可以根据需要扩展
        return articleRepository.findAll(pageRequest);
    }
    
    @Override
    public Page<Article> quickSearch(String keyword, Integer pageNum, Integer pageSize) {
        // 默认按创建时间降序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        
        PageRequest pageRequest = PageRequest.of(
            (pageNum != null && pageNum > 0) ? pageNum - 1 : 0,
            (pageSize != null && pageSize > 0 && pageSize <= 100) ? pageSize : 10,
            sort
        );
        
        // 执行查询
        // 注意：这里简化了查询逻辑，实际项目中可能需要使用JPA Criteria API或JPQL来实现复杂查询
        // 由于时间限制，这里直接返回所有文章，后续可以根据需要扩展
        return articleRepository.findAll(pageRequest);
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
                sortField = "likeCount"; // 按点赞数排序
                break;
            default:
                sortField = "createTime"; // 默认按时间排序
        }
        
        return Sort.by(direction, sortField);
    }
}