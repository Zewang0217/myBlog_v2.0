<!-- src/components/blog/ArticleList.vue -->
<template>
  <div class="article-list">
    <div class="header">
      <h2>{{ listTitle }}</h2>
      <div class="header-actions">
        <div class="category-filters" v-if="categories.length > 0">
          <label>分类筛选:</label>
          <button
            v-for="category in categories"
            :key="category.id"
            :class="['category-btn', { active: selectedCategoryIds.includes(category.id) }]"
            @click="toggleCategory(category.id)"
          >
            {{ category.name }}({{ category.articleCount || 0 }})
          </button>
          <button
            class="clear-categories-btn"
            @click="clearCategories"
            v-if="selectedCategoryIds.length > 0"
          >
            清除筛选
          </button>
        </div>

        <button @click="goToCreate" class="create-btn">
          创建文章
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      加载中...
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error">
      错误: {{ error }}
      <button @click="loadArticles">重试</button>
    </div>

    <!-- 文章列表 -->
    <div v-else class="articles">
      <div
        v-for="article in articles"
        :key="article.id"
        class="article-item"
      >
        <h3 class="article-title">{{ article.title }}</h3>
        <p class="article-meta">
          作者: {{ article.author }} |
          发布时间: {{ formatDate(article.createTime) }} |
          状态: {{ getStatusText(article.status) }}
          <span v-if="article.categories && article.categories.length > 0">
            | 分类: {{ article.categories.map(c => c.name).join(', ') }}
          </span>
        </p>
        <!-- 文章内容预览 -->
        <div class="article-preview" v-html="renderPreview(article.content)"></div>
        <div class="article-actions">
          <button @click="viewArticle(article.id)" class="view-btn">查看</button>
          <button
            v-if="(typeof article.status === 'number' && article.status === 0) || (typeof article.status === 'string' && article.status.toUpperCase() === 'DRAFT')"
            @click="publishArticle(article.id)"
            class="publish-btn"
          >
            发布
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="articles.length === 0" class="empty-state">
        暂无文章
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, watch } from 'vue';
import { useArticles, useArticle } from '@/composables/useArticles';
import { useCategories } from '@/composables/useCategories';
import { ArticleStatus } from '@/types/article';
import { useRouter } from 'vue-router';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

// 使用文章列表组合式函数
const { articles, loading, error, fetchArticles, fetchPublishedArticles, fetchDraftArticles } = useArticles();
const { publish } = useArticle();
const { categories, fetchCategories, getArticlesByCategories } = useCategories();
const router = useRouter();

// 当前筛选条件
const currentFilter = ref('all');
const selectedCategoryIds = ref<string[]>([]);

// 计算总文章数量
const totalArticleCount = computed(() => {
  return articles.value.length;
});

// 列表标题
const listTitle = computed(() => {
  let title = '';
  switch (currentFilter.value) {
    case 'published':
      title = '已发布文章';
      break;
    case 'draft':
      title = '草稿箱';
      break;
    default:
      title = '全部文章';
  }

  if (selectedCategoryIds.value.length > 0) {
    const categoryNames = categories.value
    .filter(c => selectedCategoryIds.value.includes(c.id))
    .map(c => c.name);
    title += ` (${categoryNames.join(', ')})`;
  } else {
    // 当没有选择分类时，显示总文章数量
    title += ` (${totalArticleCount.value})`;
  }

  return title;
});

// 渲染文章预览内容（截取前200个字符）
const renderPreview = (content: string) => {
  if (!content) return '';
  // 截取前200个字符作为预览
  const previewText = content.length > 200 ? content.substring(0, 200) + '...' : content;
  // 使用marked将Markdown转换为HTML，并使用DOMPurify净化HTML防止XSS攻击
  const html = marked(previewText);
  return DOMPurify.sanitize(html);
};

// 获取文章列表（根据筛选条件）
const loadArticles = async () => {
  // 如果有选中的分类，则使用分类筛选API
  if (selectedCategoryIds.value.length > 0) {
    try {
      loading.value = true;
      error.value = null;

      const response = await getArticlesByCategories(selectedCategoryIds.value);
      if (response.code === 200) {
        articles.value = response.data;
      } else {
        error.value = response.message;
      }
    } catch (err: any) {
      error.value = err.message || '获取文章列表失败';
    } finally {
      loading.value = false;
    }
    return;
  }

  try {
    loading.value = true;
    error.value = null;
    
    // 根据筛选条件加载不同文章列表
    switch (currentFilter.value) {
      case 'published':
        await fetchPublishedArticles();
        break;
      case 'draft':
        await fetchDraftArticles();
        break;
      default:
        await fetchArticles();
    }
  } catch (err: any) {
    error.value = err.message || '获取文章列表失败';
  } finally {
    loading.value = false;
  }
};

// 处理筛选条件变化
const handleFilterChange = () => {
  loadArticles();
};

// 格式化日期显示
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN');
};

// 获取状态文本
const getStatusText = (status: number | string) => {
  // 处理字符串类型的状态值
  if (typeof status === 'string') {
    switch (status.toUpperCase()) {
      case 'DRAFT':
        return '草稿';
      case 'PUBLISHED':
        return '已发布';
      case 'OFFLINE':
        return '已下架';
      default:
        return '未知';
    }
  }
  
  // 处理数字类型的状态值
  switch (status) {
    case ArticleStatus.DRAFT:
      return '草稿';
    case ArticleStatus.PUBLISHED:
      return '已发布';
    case ArticleStatus.OFFLINE:
      return '已下架';
    default:
      return '未知';
  }
};

// 跳转到创建文章页面
const goToCreate = () => {
  router.push('/article/create');
};

// 查看文章详情
const viewArticle = (id: string) => {
  router.push(`/article/${id}`);
};

// 发布文章
const publishArticle = async (id: string) => {
  if (confirm('确定要发布这篇文章吗？')) {
    try {
      const result = await publish(id);
      if (result.success) {
        alert('文章发布成功');
        loadArticles();
      } else {
        alert(`发布失败: ${result.error}`);
      }
    } catch (err) {
      console.error('发布文章失败:', err);
      alert('发布文章失败');
    }
  }
};

// 切换分类选择
const toggleCategory = (categoryId: string) => {
  const index = selectedCategoryIds.value.indexOf(categoryId);
  if (index > -1) {
    selectedCategoryIds.value.splice(index, 1);
  } else {
    selectedCategoryIds.value.push(categoryId);
  }
  // 重新加载文章
  loadArticles();
};

// 清除分类筛选
const clearCategories = () => {
  selectedCategoryIds.value = [];
  loadArticles();
};

// 组件挂载时获取数据
onMounted(() => {
  loadArticles();
  fetchCategories();
});
</script>

<style scoped>
/* 深色主题支持 */
[data-theme="dark"] .article-list {
  background: var(--background-secondary);
}

[data-theme="dark"] .header {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .header h2 {
  background: linear-gradient(135deg, #818cf8 0%, #a78bfa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

[data-theme="dark"] .category-filters {
  background: var(--background-glass);
}

[data-theme="dark"] .category-filters label {
  color: var(--text-color-secondary);
}

[data-theme="dark"] .category-btn {
  background: linear-gradient(135deg, var(--background-secondary), var(--background-glass));
  color: var(--text-color-primary);
}

[data-theme="dark"] .category-btn:hover {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
  color: white;
}

[data-theme="dark"] .category-btn.active {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
  color: white;
}

[data-theme="dark"] .clear-categories-btn {
  background: var(--background-glass);
  color: var(--text-color-primary);
  border: 2px solid #ef4444;
}

[data-theme="dark"] .clear-categories-btn:hover {
  background: #ef4444;
  color: white;
}

[data-theme="dark"] .create-btn {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
}

[data-theme="dark"] .article-item {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .article-title {
  background: linear-gradient(135deg, var(--text-color-primary), var(--text-color-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

[data-theme="dark"] .article-meta {
  color: var(--text-color-light);
}

[data-theme="dark"] .article-preview {
  color: var(--text-color-secondary);
  background: var(--background-secondary);
}

[data-theme="dark"] .view-btn {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
}

[data-theme="dark"] .publish-btn {
  background: linear-gradient(135deg, #10b981, #059669);
}

[data-theme="dark"] .error {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(220, 38, 38, 0.1));
  color: #f87171;
  border: 2px solid rgba(239, 68, 68, 0.3);
}

[data-theme="dark"] .empty-state {
  color: var(--text-color-light);
  background: var(--background-glass);
}

/* 现代化博客样式 - 玻璃拟态风格 */
.article-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 40px;
  flex-wrap: wrap;
  gap: 20px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.header h2 {
  margin: 0;
  font-size: 2.2em;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.header-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: flex-end;
}

.category-filters {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  background: rgba(255, 255, 255, 0.6);
  padding: 15px;
  border-radius: 12px;
  backdrop-filter: blur(5px);
}

.category-filters label {
  font-weight: 600;
  white-space: nowrap;
  color: #4a5568;
  font-size: 0.95em;
}

.category-btn {
  padding: 8px 16px;
  border: 2px solid transparent;
  border-radius: 25px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  cursor: pointer;
  font-size: 0.9em;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.category-btn.active {
  background: linear-gradient(135deg, #42b983, #359c6d);
  color: white;
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 20px rgba(66, 185, 131, 0.4);
}

.clear-categories-btn {
  padding: 8px 16px;
  border: 2px solid #ff6b6b;
  border-radius: 25px;
  background: rgba(255, 107, 107, 0.1);
  color: #ff6b6b;
  cursor: pointer;
  font-size: 0.9em;
  font-weight: 500;
  transition: all 0.3s ease;
}

.clear-categories-btn:hover {
  background: #ff6b6b;
  color: white;
  transform: translateY(-1px);
}

.filter-select {
  padding: 10px 15px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  background: white;
  font-size: 0.95em;
  font-weight: 500;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.filter-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.create-btn, .refresh-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 0.95em;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.create-btn {
  background: linear-gradient(135deg, #42b983, #359c6d);
  color: white;
}

.create-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(66, 185, 131, 0.4);
}

.refresh-btn {
  background: linear-gradient(135deg, #6c757d, #5a6268);
  color: white;
}

.refresh-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(108, 117, 125, 0.4);
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.loading, .error, .empty-state {
  text-align: center;
  padding: 60px 20px;
  font-size: 1.1em;
}

.loading {
  color: #667eea;
  font-weight: 500;
}

.loading::after {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-left: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  color: #e53e3e;
  background: linear-gradient(135deg, #fff5f5, #fed7d7);
  border: 2px solid #feb2b2;
  border-radius: 16px;
  margin: 20px 0;
  font-weight: 500;
}

.error button {
  margin-top: 15px;
  padding: 8px 20px;
  background: #e53e3e;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.error button:hover {
  background: #c53030;
  transform: translateY(-1px);
}

.empty-state {
  color: #718096;
  font-style: italic;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 16px;
  padding: 40px;
  backdrop-filter: blur(5px);
}

.articles {
  display: grid;
  gap: 25px;
}

.article-item {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.article-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.article-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.article-item:hover::before {
  transform: scaleX(1);
}

.article-title {
  margin: 0 0 15px 0;
  font-size: 1.6em;
  font-weight: 700;
  color: #2d3748;
  line-height: 1.3;
  background: linear-gradient(135deg, #2d3748, #4a5568);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.article-meta {
  margin: 0 0 20px 0;
  color: #718096;
  font-size: 0.95em;
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.article-preview {
  margin-bottom: 25px;
  color: #4a5568;
  line-height: 1.7;
  font-size: 1.05em;
  background: rgba(255, 255, 255, 0.5);
  padding: 20px;
  border-radius: 12px;
  backdrop-filter: blur(5px);
}

.article-preview :deep(h1),
.article-preview :deep(h2),
.article-preview :deep(h3) {
  font-size: 1.2em;
  margin: 1em 0;
  color: #2d3748;
  font-weight: 600;
}

.article-preview :deep(p) {
  margin: 1em 0;
}

.article-preview :deep(code) {
  background: rgba(0, 0, 0, 0.05);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 0.9em;
}

.article-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.view-btn,
.publish-btn {
  padding: 10px 20px;
  border-radius: 12px;
  cursor: pointer;
  font-size: 0.95em;
  font-weight: 600;
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.view-btn {
  background: linear-gradient(135deg, #409eff, #3082e0);
  color: white;
}

.view-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

.publish-btn {
  background: linear-gradient(135deg, #42b983, #359c6d);
  color: white;
}

.publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(66, 185, 131, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .article-list {
    padding: 20px 15px;
  }
  
  .header {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }
  
  .header-actions {
    align-items: center;
  }
  
  .category-filters {
    justify-content: center;
  }
  
  .article-item {
    padding: 20px;
  }
  
  .article-title {
    font-size: 1.4em;
  }
}
</style>
</file>
