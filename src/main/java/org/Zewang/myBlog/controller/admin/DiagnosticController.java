package org.Zewang.myBlog.controller.admin;

import java.util.ArrayList;
import org.Zewang.myBlog.common.ApiResponse;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.repository.ArticleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 诊断控制器 - 用于检查数据库数据格式
 */
@RestController
@RequestMapping("/api/diagnostic")
@PreAuthorize("permitAll()")
public class DiagnosticController {

    private final ArticleRepository articleRepository;

    public DiagnosticController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping("/article-status-types")
    public ApiResponse<Map<String, Object>> checkArticleStatusTypes() {
        try {
            List<Article> articles = articleRepository.findAll();
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalArticles", articles.size());
            
            // 检查状态字段的类型
            Map<String, Integer> statusTypeCounts = new HashMap<>();
            Map<String, List<String>> statusExamples = new HashMap<>();
            
            for (Article article : articles) {
                String statusType = article.getStatus() == null ? "null" : article.getStatus().getClass().getSimpleName();
                String statusValue = article.getStatus() == null ? "null" : article.getStatus().toString();
                
                statusTypeCounts.put(statusType, statusTypeCounts.getOrDefault(statusType, 0) + 1);
                
                if (!statusExamples.containsKey(statusType)) {
                    statusExamples.put(statusType, new ArrayList<>());
                }
                if (statusExamples.get(statusType).size() < 3) { // 只保留3个示例
                    statusExamples.get(statusType).add("Article " + article.getId() + ": " + statusValue);
                }
            }
            
            result.put("statusTypeCounts", statusTypeCounts);
            result.put("statusExamples", statusExamples);
            
            // 检查原始数据（绕过转换器）
            List<Map> rawArticles = articleRepository.findAll().stream()
                .map(article -> {
                    Map<String, Object> rawData = new HashMap<>();
                    rawData.put("id", article.getId());
                    rawData.put("title", article.getTitle());
                    rawData.put("status", article.getStatus()); // 这将触发转换器
                    rawData.put("statusClass", article.getStatus() == null ? "null" : article.getStatus().getClass().getName());
                    return rawData;
                })
                .collect(Collectors.toList());
            
            result.put("sampleData", rawArticles.subList(0, Math.min(5, rawArticles.size())));
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            errorResult.put("errorClass", e.getClass().getSimpleName());
            
            // 尝试获取更详细的错误信息
            Throwable cause = e.getCause();
            if (cause != null) {
                errorResult.put("cause", cause.getMessage());
                errorResult.put("causeClass", cause.getClass().getSimpleName());
            }
            
            return ApiResponse.success(errorResult);
        }
    }
}