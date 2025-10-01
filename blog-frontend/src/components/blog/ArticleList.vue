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
            {{ category.name }}
          </button>
          <button
            class="clear-categories-btn"
            @click="clearCategories"
            v-if="selectedCategoryIds.length > 0"
          >
            清除筛选
          </button>
        </div>

        <select v-model="currentFilter" @change="handleFilterChange" class="filter-select">
          <option value="all">全部文章</option>
          <option value="published">已发布</option>
          <option value="draft">草稿</option>
        </select>
        <button @click="goToCreate" class="create-btn">
          创建文章
        </button>
        <button @click="loadArticles" :disabled="loading" class="refresh-btn">
          {{ loading ? '刷新中...' : '刷新' }}
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
            v-if="article.status === 0 /* DRAFT */"
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
const selectedCategoryIds = ref<number[]>([]);

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
const getStatusText = (status: number) => {
  switch (status) {
    case ArticleStatus.DRAFT:
      return '草稿';
    case ArticleStatus.PUBLISHED:
      return '已发布';
    default:
      return '未知';
  }
};

// 跳转到创建文章页面
const goToCreate = () => {
  router.push('/article/create');
};

// 查看文章详情
const viewArticle = (id: number) => {
  router.push(`/article/${id}`);
};

// 发布文章
const publishArticle = async (id: number) => {
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
const toggleCategory = (categoryId: number) => {
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
.article-list {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.header h2 {
  margin: 0;
  font-size: 1.5em;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.category-filters {
  display: flex;
  align-items: center;
  gap: 5px;
  flex-wrap: wrap;
}

.category-filters label {
  font-weight: bold;
  white-space: nowrap;
}

.category-btn {
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 15px;
  background-color: #f8f9fa;
  cursor: pointer;
  font-size: 0.9em;
  transition: all 0.2s;
}

.category-btn:hover {
  background-color: #e9ecef;
}

.category-btn.active {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}

.clear-categories-btn {
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 15px;
  background-color: #f8f9fa;
  cursor: pointer;
  font-size: 0.9em;
}

.clear-categories-btn:hover {
  background-color: #e9ecef;
}

.filter-select {
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
}

.create-btn, .refresh-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9em;
}

.create-btn {
  background-color: #42b983;
  color: white;
}

.create-btn:hover:not(:disabled) {
  background-color: #359c6d;
}

.refresh-btn {
  background-color: #6c757d;
  color: white;
}

.refresh-btn:hover:not(:disabled) {
  background-color: #5a6268;
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading, .error, .empty-state {
  text-align: center;
  padding: 40px 20px;
}

.loading {
  color: #666;
}

.error {
  color: #e53935;
  background-color: #ffebee;
  border: 1px solid #ffcdd2;
  border-radius: 4px;
  margin: 20px 0;
}

.empty-state {
  color: #999;
}

.articles {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.article-title {
  margin: 0 0 10px 0;
  font-size: 1.3em;
  color: #333;
}

.article-meta {
  margin: 0 0 15px 0;
  color: #666;
  font-size: 0.9em;
}

.article-preview {
  margin-bottom: 15px;
  color: #444;
  line-height: 1.6;
}

.article-preview :deep(h1),
.article-preview :deep(h2),
.article-preview :deep(h3) {
  font-size: 1.1em;
  margin: 0.8em 0;
}

.article-preview :deep(p) {
  margin: 0.8em 0;
}

.article-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.view-btn,
.publish-btn {
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9em;
  border: none;
}

.view-btn {
  background-color: #409eff;
  color: white;
}

.view-btn:hover {
  background-color: #3082e0;
}

.publish-btn {
  background-color: #42b983;
  color: white;
}

.publish-btn:hover {
  background-color: #359c6d;
}
</style>
</file>
