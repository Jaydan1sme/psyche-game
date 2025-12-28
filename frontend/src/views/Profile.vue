<template>
  <div class="profile-container">
    <h2>个人中心</h2>
    
    <!-- 个人信息 -->
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h3>基本信息</h3>
        </div>
      </template>
      <div class="profile-info">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="userInfo.avatar" class="user-avatar" />
          </div>
          <el-button class="upload-btn">
            <el-icon><Upload /></el-icon> 上传头像
          </el-button>
        </div>
        <div class="info-section">
          <el-form label-position="top">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled class="light-input" />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="userInfo.nickname" class="light-input" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" class="light-input" />
            </el-form-item>
            <el-form-item label="注册时间">
              <el-input v-model="userInfo.registerTime" disabled class="light-input" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave" class="save-btn">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
    
    <!-- 游戏数据统计 -->
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h3>游戏数据统计</h3>
        </div>
      </template>
      <div class="game-stats">
        <div class="stat-item" v-for="stat in gameStats" :key="stat.gameType">
          <div class="stat-icon">
            <game-icons :type="stat.gameType" :size="48" />
          </div>
          <h4>{{ stat.gameName }}</h4>
          <div class="stat-value">
            <span class="label">最高分数:</span>
            <span class="score">{{ stat.highestScore }}</span>
          </div>
          <div class="stat-value">
            <span class="label">游戏次数:</span>
            <span class="count">{{ stat.playCount }}</span>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 发布的论坛文章 -->
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h3>我的文章</h3>
        </div>
      </template>
      <div class="my-articles">
        <div class="article-item" v-for="article in myArticles" :key="article.id">
          <h4 class="article-title">{{ article.title }}</h4>
          <div class="article-meta">
            <span class="time">{{ formatTime(article.createdAt) }}</span>
            <el-tag :type="article.status === 'published' ? 'success' : 'warning'" size="small">
              {{ article.status === 'published' ? '已发布' : '草稿' }}
            </el-tag>
          </div>
          <div class="article-actions">
            <el-button size="small" class="edit-btn">编辑</el-button>
            <el-button size="small" type="danger" class="delete-btn">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import GameIcons from '@/components/common/GameIcons.vue'

// 响应式数据
const userInfo = ref({
  username: 'testuser',
  nickname: '游戏达人',
  email: 'test@example.com',
  avatar: '',
  registerTime: '2025-01-01 10:00:00'
})

// 游戏数据统计
const gameStats = ref([
  { gameType: 'sweeper', gameName: '扫雷', highestScore: 999, playCount: 50 },
  { gameType: 'snake', gameName: '贪吃蛇', highestScore: 2048, playCount: 120 },
  { gameType: 'airplane', gameName: '打飞机', highestScore: 15000, playCount: 30 },
  { gameType: '2048', gameName: '2048', highestScore: 4096, playCount: 80 }
])

// 我的文章
const myArticles = ref([
  { id: 1, title: '扫雷游戏高级技巧分享', createdAt: '2025-01-15 14:30:00', status: 'published' },
  { id: 2, title: '贪吃蛇AI算法实现', createdAt: '2025-01-10 10:00:00', status: 'published' }
])

// 方法
const handleSave = () => {
  console.log('保存用户信息')
}

const formatTime = (date) => {
  return date
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-container h2 {
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

.profile-container h2::after {
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

.profile-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.profile-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

/* 个人信息 */
.profile-info {
  display: flex;
  gap: 40px;
}

@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
  }
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  width: 120px;
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  border-color: var(--gradient-start);
  box-shadow: 0 0 15px rgba(122, 134, 232, 0.2);
  transform: rotate(5deg);
}

.user-avatar {
  border-radius: 50%;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover .user-avatar {
  transform: scale(1.05);
}

.upload-btn {
  margin-top: 8px;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  border-radius: var(--border-radius);
  padding: 8px 16px;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  border-color: var(--gradient-start);
  background-color: var(--primary-bg);
  color: var(--accent-color);
}

.info-section {
  flex: 1;
}

/* 浅色输入框 */
.light-input .el-input__wrapper {
  background-color: var(--bg-secondary);
  border: none;
  border-bottom: 2px solid transparent;
  border-radius: 8px;
  box-shadow: none;
  transition: all 0.3s ease;
}

.light-input .el-input__wrapper:focus {
  background-color: var(--primary-bg);
  border-bottom: 2px solid var(--gradient-start);
  box-shadow: none;
}

/* 保存按钮 */
.save-btn {
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  border: none;
  border-radius: var(--border-radius);
  padding: 10px 24px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-light);
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
  background: linear-gradient(120deg, var(--gradient-end), var(--gradient-start));
}

/* 游戏数据统计 */
.game-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.stat-item {
  padding: 20px;
  background-color: var(--bg-secondary);
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: var(--gradient-start);
}

.stat-icon {
  margin-bottom: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.stat-item h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.stat-value {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.stat-value .label {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 400;
}

.stat-value .score {
  font-size: 24px;
  font-weight: 600;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-value .count {
  font-size: 20px;
  font-weight: 600;
  color: var(--accent-color);
}

/* 我的文章 */
.my-articles {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-item {
  padding: 20px;
  background-color: var(--bg-secondary);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
}

.article-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: var(--gradient-start);
}

.article-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  transition: all 0.3s ease;
}

.article-item:hover .article-title {
  color: var(--accent-color);
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: var(--text-secondary);
}

.article-actions {
  align-self: flex-end;
  display: flex;
  gap: 12px;
}

.edit-btn {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  border-radius: var(--border-radius);
  transition: all 0.3s ease;
}

.edit-btn:hover {
  border-color: var(--gradient-start);
  color: var(--accent-color);
}

.delete-btn {
  border: 1px solid var(--error-color);
  background-color: rgba(244, 67, 54, 0.05);
  color: var(--error-color);
  border-radius: var(--border-radius);
  transition: all 0.3s ease;
}

.delete-btn:hover {
  background-color: var(--error-color);
  color: white;
}
</style>