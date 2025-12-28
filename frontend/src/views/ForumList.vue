<template>
  <div class="forum-list-container">
    <h2>论坛</h2>
    
    <!-- 文章发布按钮 -->
    <el-button type="primary" class="publish-btn" @click="handlePublish">
      <el-icon><Plus /></el-icon> 发布文章
    </el-button>
    
    <!-- 文章列表 -->
    <div class="article-list">
      <div class="article-item" v-for="article in articles" :key="article.id">
        <div class="article-header">
          <h3 class="article-title" @click="handleViewDetail(article.id)">{{ article.title }}</h3>
          <div class="article-meta">
            <span class="author">{{ article.author }}</span>
            <span class="time">{{ formatTime(article.createdAt) }}</span>
            <el-tag :type="article.status === 'published' ? 'success' : 'warning'">
              {{ article.status === 'published' ? '已发布' : '草稿' }}
            </el-tag>
          </div>
        </div>
        <div class="article-content">
          {{ article.content.substring(0, 100) }}...
        </div>
        <div class="article-stats">
          <span class="views"><el-icon><view /></el-icon> {{ article.viewCount }}</span>
          <span class="likes"><el-icon><star /></el-icon> {{ article.likeCount }}</span>
          <span class="comments"><el-icon><chat-dot-round /></el-icon> {{ article.commentCount }}</span>
        </div>
      </div>
    </div>
    
    <!-- 分页 -->
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="totalArticles"
      layout="prev, pager, next, jumper, total"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 响应式数据
const currentPage = ref(1)
const pageSize = ref(10)
const totalArticles = ref(25)

// 模拟文章数据
const articles = ref([
  {
    id: 1,
    title: '扫雷游戏高级技巧分享',
    content: '分享扫雷游戏的高级技巧，帮助你成为扫雷大师...',
    author: '游戏达人',
    createdAt: new Date(),
    status: 'published',
    viewCount: 1234,
    likeCount: 56,
    commentCount: 23
  },
  {
    id: 2,
    title: '贪吃蛇AI算法实现',
    content: '使用AI算法实现贪吃蛇自动寻路，让电脑帮你玩游戏...',
    author: '技术大牛',
    createdAt: new Date(Date.now() - 3600000),
    status: 'published',
    viewCount: 892,
    likeCount: 34,
    commentCount: 15
  }
])

// 方法
const handlePublish = () => {
  // 跳转到发布文章页面
  console.log('发布文章')
}

const handleViewDetail = (articleId) => {
  router.push(`/forum/article/${articleId}`)
}

const formatTime = (date) => {
  return new Date(date).toLocaleString()
}

const handlePageChange = (page) => {
  currentPage.value = page
  // 调用API获取对应页码的文章
  console.log('切换到第', page, '页')
}
</script>

<style scoped>
.forum-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.forum-list-container h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 30px;
  position: relative;
  padding-bottom: 10px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-align: center;
}

.forum-list-container h2::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  border-radius: 2px;
}

.publish-btn {
  margin-bottom: 24px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  border: none;
  border-radius: var(--border-radius);
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-light);
}

.publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
  background: linear-gradient(120deg, var(--gradient-end), var(--gradient-start));
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 24px;
}

.article-item {
  background-color: var(--primary-bg);
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.article-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: var(--gradient-start);
}

.article-header {
  margin-bottom: 16px;
}

.article-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  line-height: 1.4;
}

.article-title:hover {
  color: var(--accent-color);
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: var(--text-secondary);
}

.article-meta .author {
  font-weight: 500;
  color: var(--accent-color);
}

.article-content {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-stats {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 14px;
  color: var(--text-secondary);
}

.article-stats span {
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.article-stats span:hover {
  color: var(--accent-color);
}
</style>