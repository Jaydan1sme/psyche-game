<template>
  <div class="article-detail-container">
    <el-card class="article-card">
      <div class="article-header">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="author">{{ article.author }}</span>
          <span class="time">{{ formatTime(article.createdAt) }}</span>
          <span class="views"><el-icon><view /></el-icon> {{ article.viewCount }}</span>
        </div>
      </div>
      
      <div class="article-content">
        {{ article.content }}
      </div>
      
      <div class="article-actions">
        <el-button type="primary" @click="handleLike" :icon="isLiked ? 'StarFilled' : 'Star'">
          {{ isLiked ? '已点赞' : '点赞' }}
          <span class="like-count">{{ article.likeCount }}</span>
        </el-button>
        <el-button type="default" @click="handleShare" :icon="'Share'">
          分享
        </el-button>
        <el-button type="default" @click="handleCollect" :icon="isCollected ? 'CollectionFilled' : 'Collection'">
          {{ isCollected ? '已收藏' : '收藏' }}
        </el-button>
      </div>
    </el-card>
    
    <!-- 评论区 -->
    <el-card class="comment-card">
      <template #header>
        <div class="card-header">
          <h3>评论区 ({{ comments.length }})</h3>
        </div>
      </template>
      
      <!-- 评论输入 -->
      <div class="comment-input-section">
        <el-input
          v-model="commentContent"
          type="textarea"
          rows="3"
          placeholder="写下你的评论..."
        />
        <div class="comment-input-actions">
          <el-button type="primary" @click="handleSubmitComment" :disabled="!commentContent.trim()">
            发布评论
          </el-button>
        </div>
      </div>
      
      <!-- 评论列表 -->
      <div class="comment-list">
        <div class="comment-item" v-for="comment in comments" :key="comment.id">
          <div class="comment-avatar">
            <el-avatar :size="40" :src="comment.avatar" />
          </div>
          <div class="comment-content-wrapper">
            <div class="comment-header">
              <span class="comment-author">{{ comment.author }}</span>
              <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button type="text" size="small" @click="handleReply(comment.id)">
                <el-icon><ChatDotRound /></el-icon> 回复
              </el-button>
              <el-button type="text" size="small" @click="handleLikeComment(comment.id)">
                <el-icon><Star /></el-icon> {{ comment.likeCount }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 响应式数据
const article = ref({
  id: 0,
  title: '加载中...',
  content: '',
  author: '',
  createdAt: new Date(),
  viewCount: 0,
  likeCount: 0,
  commentCount: 0
})

const comments = ref([
  {
    id: 1,
    author: '用户1',
    avatar: '',
    content: '这篇文章写得很好！',
    createdAt: new Date(Date.now() - 3600000),
    likeCount: 5
  },
  {
    id: 2,
    author: '用户2',
    avatar: '',
    content: '感谢分享，学到了很多！',
    createdAt: new Date(Date.now() - 7200000),
    likeCount: 3
  }
])

const isLiked = ref(false)
const isCollected = ref(false)
const commentContent = ref('')

// 方法
const formatTime = (date) => {
  return new Date(date).toLocaleString()
}

const handleLike = () => {
  isLiked.value = !isLiked.value
  article.value.likeCount += isLiked.value ? 1 : -1
  console.log('点赞状态:', isLiked.value)
}

const handleShare = () => {
  console.log('分享文章')
}

const handleCollect = () => {
  isCollected.value = !isCollected.value
  console.log('收藏状态:', isCollected.value)
}

const handleSubmitComment = () => {
  if (!commentContent.value.trim()) return
  
  // 模拟发布评论
  const newComment = {
    id: comments.value.length + 1,
    author: '当前用户',
    avatar: '',
    content: commentContent.value,
    createdAt: new Date(),
    likeCount: 0
  }
  
  comments.value.unshift(newComment)
  article.value.commentCount++
  commentContent.value = ''
  console.log('发布评论成功')
}

const handleReply = (commentId) => {
  console.log('回复评论:', commentId)
}

const handleLikeComment = (commentId) => {
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    comment.likeCount++
  }
  console.log('点赞评论:', commentId)
}

// 生命周期钩子
onMounted(() => {
  // 根据路由参数获取文章详情
  const articleId = route.params.id
  console.log('获取文章详情:', articleId)
  
  // 模拟文章数据
  setTimeout(() => {
    article.value = {
      id: articleId,
      title: '扫雷游戏高级技巧分享',
      content: '扫雷是一款经典的单人益智游戏，考验玩家的推理能力和判断速度。本文将分享一些扫雷游戏的高级技巧，帮助你成为扫雷大师。\n\n1. 基本规则回顾\n扫雷游戏的目标是找出所有地雷，避免触雷。游戏界面由许多方格组成，点击方格可以揭示其内容。如果方格中是数字，则表示周围8个方格中地雷的数量；如果是地雷，则游戏结束。\n\n2. 高级技巧\n2.1 模式识别\n学会识别常见的地雷模式，如1-2-1模式、1-2-2-1模式等，可以快速判断地雷位置。\n\n2.2 概率计算\n在不确定的情况下，计算每个方格是地雷的概率，选择概率最低的方格点击。\n\n2.3 标记技巧\n合理使用标记功能，标记可疑的地雷位置，避免误点。\n\n3. 实战演练\n通过实际案例演示如何运用这些技巧，提高扫雷速度和准确率。\n\n4. 总结\n扫雷游戏需要耐心和技巧，通过不断练习和掌握高级技巧，你可以成为扫雷高手。',
      author: '游戏达人',
      createdAt: new Date(Date.now() - 86400000),
      viewCount: 1234,
      likeCount: 56,
      commentCount: comments.value.length
    }
  }, 500)
})
</script>

<style scoped>
.article-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.article-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.article-header {
  margin-bottom: 20px;
}

.article-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
  line-height: 1.4;
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
  color: var(--primary-color);
}

.article-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.article-content {
  font-size: 16px;
  color: var(--text-primary);
  line-height: 1.8;
  margin-bottom: 24px;
  white-space: pre-wrap;
}

.article-actions {
  display: flex;
  gap: 12px;
}

.article-actions .like-count {
  margin-left: 4px;
}

.comment-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.comment-input-section {
  margin-bottom: 24px;
}

.comment-input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background-color: var(--bg-secondary);
  border-radius: 8px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary);
}

.comment-header .comment-author {
  font-weight: 500;
  color: var(--primary-color);
}

.comment-text {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.comment-actions button {
  padding: 0;
  font-size: 12px;
  color: var(--text-secondary);
}
</style>