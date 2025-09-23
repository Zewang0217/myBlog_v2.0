package org.Zewang.myBlog.service.article;

import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.dao.ArticleMapper;
import org.Zewang.myBlog.dto.CreateArticleDTO;
import org.Zewang.myBlog.model.Article;
import org.Zewang.myBlog.model.enums.ArticleStatus;
import org.Zewang.myBlog.service.article.impl.ArticleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("文章服务测试")
class ArticleServiceTest {

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private Article testArticle;
    private CreateArticleDTO testCreateDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试文章
        testArticle = new Article()
            .setId(1L)
            .setTitle("测试标题")
            .setContent("测试内容")
            .setAuthor("测试作者")
            .setStatus(ArticleStatus.DRAFT)
            .setCreateTime(LocalDateTime.now())
            .setUpdateTime(LocalDateTime.now());

        // 初始化创建文章DTO
        testCreateDTO = new CreateArticleDTO(
            "测试标题",
            "测试内容",
            "测试作者"
        );
    }

    @Test
    @DisplayName("获取所有文章 - 成功")
    void getAllArticles_ShouldReturnArticleList() {
        // 准备
        List<Article> expectedArticles = Arrays.asList(testArticle);
        when(articleMapper.findAll()).thenReturn(expectedArticles);

        // 执行
        List<Article> result = articleService.getAllArticles();

        // 验证
        assertNotNull(result, "返回的文章列表不应为null");
        assertEquals(1, result.size(), "返回的文章数量不正确");
        assertEquals("测试标题", result.get(0).getTitle(), "文章标题不匹配");
        verify(articleMapper, times(1)).findAll();
    }

    @Test
    @DisplayName("根据ID获取文章 - 文章存在")
    void getById_WhenArticleExists_ShouldReturnArticle() {
        // 准备
        when(articleMapper.findById(1L)).thenReturn(testArticle);

        // 执行
        Optional<Article> result = articleService.getById(1L);

        // 验证
        assertTrue(result.isPresent(), "应该返回非空的Optional");
        assertEquals("测试标题", result.get().getTitle(), "文章标题不匹配");
        verify(articleMapper, times(1)).findById(1L);
    }

    @Test
    @DisplayName("根据ID获取文章 - 文章不存在")
    void getById_WhenArticleNotExists_ShouldReturnEmpty() {
        // 准备
        when(articleMapper.findById(999L)).thenReturn(null);

        // 执行
        Optional<Article> result = articleService.getById(999L);

        // 验证
        assertTrue(result.isEmpty(), "当文章不存在时应返回空的Optional");
        verify(articleMapper, times(1)).findById(999L);
    }

    @Test
    @DisplayName("创建文章 - 成功")
    void createArticle_WithValidData_ShouldCreateArticle() {
        // 准备
        when(articleMapper.existsByTitle(anyString())).thenReturn(false);
        when(articleMapper.insert(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setId(1L); // 模拟插入后设置ID
            return 1;
        });

        // 执行
        Article result = articleService.createArticle(testCreateDTO);

        // 验证
        assertNotNull(result, "创建的文章不应为null");
        assertEquals("测试标题", result.getTitle(), "文章标题不匹配");
        assertEquals("测试内容", result.getContent(), "文章内容不匹配");
        assertEquals("测试作者", result.getAuthor(), "文章作者不匹配");
        assertEquals(ArticleStatus.DRAFT, result.getStatus(), "新文章默认状态应为草稿");
        assertNotNull(result.getCreateTime(), "创建时间不应为null");
        assertNotNull(result.getUpdateTime(), "更新时间不应为null");

        // 验证方法调用
        verify(articleMapper, times(1)).existsByTitle("测试标题");
        verify(articleMapper, times(1)).insert(any(Article.class));
    }

    @Test
    @DisplayName("创建文章 - 标题已存在")
    void createArticle_WithDuplicateTitle_ShouldThrowException() {
        // 准备
        when(articleMapper.existsByTitle("测试标题")).thenReturn(true);

        // 执行和验证
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> articleService.createArticle(testCreateDTO),
            "当标题已存在时应抛出BusinessException"
        );

        assertEquals("文章标题已存在", exception.getMessage(), "异常消息不匹配");
        verify(articleMapper, times(1)).existsByTitle("测试标题");
        verify(articleMapper, never()).insert(any(Article.class));
    }

    @Test
    @DisplayName("更新文章 - 成功")
    void updateArticle_WithValidData_ShouldUpdateArticle() {
        // 准备
        when(articleMapper.findById(1L)).thenReturn(testArticle);
        when(articleMapper.existsByTitleAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(articleMapper.update(any(Article.class))).thenReturn(1);

        // 更新后的数据
        CreateArticleDTO updateDTO = new CreateArticleDTO(
            "更新后的标题",
            "更新后的内容",
            "更新后的作者"
        );

        // 执行
        Article result = articleService.updateArticle(1L, updateDTO);

        // 验证
        assertNotNull(result, "更新的文章不应为null");
        assertEquals("更新后的标题", result.getTitle(), "文章标题未更新");
        assertEquals("更新后的内容", result.getContent(), "文章内容未更新");
        assertEquals("更新后的作者", result.getAuthor(), "文章作者未更新");
        assertNotNull(result.getUpdateTime(), "更新时间不应为null");

        // 验证方法调用
        verify(articleMapper, times(1)).findById(1L);
        verify(articleMapper, times(1)).existsByTitleAndIdNot("更新后的标题", 1L);
        verify(articleMapper, times(1)).update(any(Article.class));
    }

    @Test
    @DisplayName("删除文章 - 成功")
    void deleteArticle_WhenArticleExists_ShouldDeleteArticle() {
        // 准备
        when(articleMapper.existsById(1L)).thenReturn(true);
        when(articleMapper.deleteById(1L)).thenReturn(1);

        // 执行
        articleService.deleteArticle(1L);

        // 验证
        verify(articleMapper, times(1)).existsById(1L);
        verify(articleMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("删除文章 - 文章不存在")
    void deleteArticle_WhenArticleNotExists_ShouldThrowException() {
        // 准备
        when(articleMapper.existsById(999L)).thenReturn(false);

        // 执行和验证
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> articleService.deleteArticle(999L),
            "当文章不存在时应抛出BusinessException"
        );

        assertEquals("文章不存在或已被删除", exception.getMessage(), "异常消息不匹配");
        verify(articleMapper, times(1)).existsById(999L);
        verify(articleMapper, never()).deleteById(anyLong());
    }
}