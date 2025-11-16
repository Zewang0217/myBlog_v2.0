package org.Zewang.myBlog.service.article;

import org.Zewang.myBlog.common.exception.BusinessException;
import org.Zewang.myBlog.repository.ArticleRepository;
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
import org.Zewang.myBlog.model.Category;
import org.Zewang.myBlog.repository.CategoryRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("文章服务测试")
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private Article testArticle;
    private CreateArticleDTO testCreateDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试文章
        testArticle = new Article();
        testArticle.setTitle("测试标题");
        testArticle.setContent("测试内容");
        testArticle.setAuthor("测试作者");
        testArticle.setStatus(ArticleStatus.DRAFT);
        testArticle.setCreateTime(LocalDateTime.now());
        testArticle.setUpdateTime(LocalDateTime.now());

        // 初始化创建文章DTO
        testCreateDTO = new CreateArticleDTO(
            "测试标题",
            "测试内容",
            "测试作者",
            ArticleStatus.DRAFT,
            List.of("1", "2")
        );
    }

    @Test
    @DisplayName("获取所有文章 - 成功")
    void getAllArticles_ShouldReturnArticleList() {
        // 准备
        List<Article> expectedArticles = Arrays.asList(testArticle);
        when(articleRepository.findAll()).thenReturn(expectedArticles);

        // 执行
        List<Article> result = articleService.getAllArticles();

        // 验证
        assertNotNull(result, "返回的文章列表不应为null");
        assertEquals(1, result.size(), "返回的文章数量不正确");
        assertEquals("测试标题", result.get(0).getTitle(), "文章标题不匹配");
        verify(articleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("根据ID获取文章 - 文章存在")
    void getById_WhenArticleExists_ShouldReturnArticle() {
        // 准备
        when(articleRepository.findById("1")).thenReturn(Optional.of(testArticle));

        // 执行
        Optional<Article> result = articleService.getById("1");

        // 验证
        assertTrue(result.isPresent(), "应该返回非空的Optional");
        assertEquals("测试标题", result.get().getTitle(), "文章标题不匹配");
        verify(articleRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("根据ID获取文章 - 文章不存在")
    void getById_WhenArticleNotExists_ShouldReturnEmpty() {
        // 准备
        when(articleRepository.findById("999")).thenReturn(Optional.empty());

        // 执行
        Optional<Article> result = articleService.getById("999");

        // 验证
        assertTrue(result.isEmpty(), "当文章不存在时应返回空的Optional");
        verify(articleRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("创建文章 - 成功")
    void createArticle_WithValidData_ShouldCreateArticle() {
        // 初始化测试分类对象
        Category testCategory1 = new Category();
        testCategory1.setId("1");
        testCategory1.setName("分类1");
        
        Category testCategory2 = new Category();
        testCategory2.setId("2");
        testCategory2.setName("分类2");
        
        // 准备
        when(articleRepository.existsByTitle(anyString())).thenReturn(false);
        when(categoryRepository.findAllById(List.of("1", "2"))).thenReturn(List.of(testCategory1, testCategory2));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setId("1"); // 模拟插入后设置ID
            return article;
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
        verify(articleRepository, times(1)).existsByTitle("测试标题");
        verify(categoryRepository, times(1)).findAllById(List.of("1", "2"));
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    @DisplayName("创建文章 - 标题已存在")
    void createArticle_WithDuplicateTitle_ShouldThrowException() {
        // 准备
        when(articleRepository.existsByTitle("测试标题")).thenReturn(true);

        // 执行和验证
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> articleService.createArticle(testCreateDTO),
            "当标题已存在时应抛出BusinessException"
        );
// 验证
        assertEquals("文章标题已存在", exception.getMessage(), "异常消息不匹配");
        verify(articleRepository, times(1)).existsByTitle("测试标题");
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    @DisplayName("更新文章 - 成功")
    void updateArticle_WithValidData_ShouldUpdateArticle() {
        // 初始化测试分类对象
        Category testCategory1 = new Category();
        testCategory1.setId("1");
        testCategory1.setName("分类1");
        
        Category testCategory2 = new Category();
        testCategory2.setId("2");
        testCategory2.setName("分类2");
        
        // 准备
        when(articleRepository.findById("1")).thenReturn(Optional.of(testArticle));
        when(articleRepository.existsByTitleAndIdNot(anyString(), anyString())).thenReturn(false);
        when(categoryRepository.findAllById(List.of("1", "2"))).thenReturn(List.of(testCategory1, testCategory2));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 更新后的数据
        CreateArticleDTO updateDTO = new CreateArticleDTO(
            "更新后的标题",
            "更新后的内容",
            "更新后的作者",
            ArticleStatus.PUBLISHED,
            List.of("1", "2")
        );

        // 执行
        Article result = articleService.updateArticle("1", updateDTO);

        // 验证
        assertNotNull(result, "更新的文章不应为null");
        assertEquals("更新后的标题", result.getTitle(), "文章标题未更新");
        assertEquals("更新后的内容", result.getContent(), "文章内容未更新");
        assertEquals("更新后的作者", result.getAuthor(), "文章作者未更新");
        assertNotNull(result.getUpdateTime(), "更新时间不应为null");

        // 验证方法调用
        verify(articleRepository, times(1)).findById("1");
        verify(articleRepository, times(1)).existsByTitleAndIdNot("更新后的标题", "1");
        verify(categoryRepository, times(1)).findAllById(List.of("1", "2"));
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    @DisplayName("删除文章 - 成功")
    void deleteArticle_WhenArticleExists_ShouldDeleteArticle() {
        // 准备
        when(articleRepository.existsById("1")).thenReturn(true);
        doNothing().when(articleRepository).deleteById("1");

        // 执行
        articleService.deleteArticle("1");

        // 验证
        verify(articleRepository, times(1)).existsById("1");
        verify(articleRepository, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("删除文章 - 文章不存在")
    void deleteArticle_WhenArticleNotExists_ShouldThrowException() {
        // 准备
        when(articleRepository.existsById("999")).thenReturn(false);

        // 执行和验证
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> articleService.deleteArticle("999"),
            "当文章不存在时应抛出BusinessException"
        );
// 验证
        assertEquals("文章不存在或已被删除", exception.getMessage(), "异常消息不匹配");
        verify(articleRepository, times(1)).existsById("999");
        verify(articleRepository, never()).deleteById(anyString());
    }
}