package org.Zewang.myBlog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 文章搜索请求DTO
 * @email "Zewang0217@outlook.com"
 */
@Schema(description = "文章搜索请求参数")
public class SearchArticleDTO {
    
    @Schema(description = "搜索关键词", example = "Java 编程")
    private String keyword;
    
    @Schema(description = "文章分类ID")
    private String categoryId;
    
    @Schema(description = "文章作者")
    private String author;
    
    @Schema(description = "文章状态：0-草稿，1-已发布")
    private Integer status;
    
    @Schema(description = "排序方式：1-按时间排序，2-按阅读量排序，3-按点赞数排序")
    private Integer sortBy = 1;
    
    @Schema(description = "排序方向：1-升序，2-降序")
    private Integer sortDirection = 2;
    
    @Schema(description = "当前页码", example = "1")
    @Range(min = 1, message = "页码必须大于或等于1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页大小", example = "10")
    @Range(min = 1, max = 100, message = "每页大小必须在1-100之间")
    private Integer pageSize = 10;
    
    // Getters
    public String getKeyword() {
        return keyword;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public Integer getSortBy() {
        return sortBy;
    }
    
    public Integer getSortDirection() {
        return sortDirection;
    }
    
    public Integer getPageNum() {
        return pageNum;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    // Setters
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public void setSortBy(Integer sortBy) {
        this.sortBy = sortBy;
    }
    
    public void setSortDirection(Integer sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}