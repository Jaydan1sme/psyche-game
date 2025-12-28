<template>
  <div class="home-container">
    <!-- 本地/在线模式切换提示 -->
    <el-alert
      v-if="currentMode !== 'online'"
      :type="currentMode === 'local' ? 'success' : 'warning'"
      :title="modeAlertTitle"
      :closable="false"
      class="mode-alert"
    >
      {{ modeAlertContent }}
    </el-alert>
    
    <!-- 在线/离线选择弹窗（首次进入时显示） -->
    <el-dialog
      v-model="showModeDialog"
      title="选择运行模式"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      center
    >
      <div class="mode-selection">
        <div class="mode-item" @click="selectMode('online')">
          <div class="mode-icon online"><el-icon><wifi /></el-icon></div>
          <h3>在线模式</h3>
          <p>连接到远程服务器，数据同步到云端，支持实时多人游戏</p>
        </div>
        <div class="mode-item" @click="selectMode('local')">
          <div class="mode-icon local"><el-icon><hard-drive /></el-icon></div>
          <h3>本地模式</h3>
          <p>使用本地部署的服务，数据存储在本地，无需网络连接</p>
        </div>
        <div class="mode-item" @click="selectMode('offline')">
          <div class="mode-icon offline"><el-icon><wifi-off /></el-icon></div>
          <h3>离线模式</h3>
          <p>纯本地运行，数据存储在浏览器，无需任何网络连接</p>
        </div>
      </div>
    </el-dialog>
    
    <!-- 页面标题 -->
    <h1 class="page-title">欢迎来到 Psyche Game</h1>
    
    <!-- 小游戏分类入口 -->
    <section class="games-section">
      <h2 class="section-title">小游戏分类</h2>
      <div class="games-grid">
        <game-card
          v-for="game in games"
          :key="game.type"
          :game="game"
          :is-hot="game.isHot"
          @play="handlePlayGame"
        />
      </div>
    </section>
    
    <!-- 热门文章推荐轮播 -->
    <section class="forum-section">
      <h2 class="section-title">热门论坛文章</h2>
      <div class="article-carousel">
        <el-carousel height="200px" :interval="5000">
          <el-carousel-item v-for="article in hotArticles" :key="article.id">
            <div class="carousel-item" @click="handleViewArticle(article.id)">
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-excerpt">{{ article.excerpt }}</p>
              <div class="article-meta">
                <span class="author">{{ article.author }}</span>
                <span class="time">{{ formatTime(article.createdAt) }}</span>
                <span class="views"><el-icon><view /></el-icon> {{ article.viewCount }}</span>
                <span class="likes"><el-icon><star /></el-icon> {{ article.likeCount }}</span>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      <el-button type="primary" @click="goToForum" class="view-more-btn">
        查看更多 <el-icon class="el-icon--right"><arrow-right /></el-icon>
      </el-button>
    </section>
    
    <!-- 本地部署状态卡片（本地模式下显示） -->
    <section class="local-status-section" v-if="currentMode === 'local'">
      <local-status @refresh="checkLocalServices" />
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useModeStore } from '@/store/mode'
import GameCard from '@/components/business/game/GameCard.vue'
import LocalStatus from '@/components/local/LocalStatus.vue'
import { formatTime } from '@/utils/date'

const router = useRouter()
const modeStore = useModeStore()

// 响应式数据
const showModeDialog = ref(modeStore.isFirstEnter)
const localServices = ref({})

// 计算属性
const currentMode = computed(() => modeStore.currentMode)
const modeAlertTitle = computed(() => {
  return currentMode.value === 'local' ? '本地模式' : '离线模式'
})
const modeAlertContent = computed(() => {
  return currentMode.value === 'local' ? '当前运行在本地模式，数据存储在本地数据库' : '当前运行在离线模式，数据存储在浏览器本地'
})

// 模拟游戏数据
const games = ref([
  { type: 'sweeper', name: '扫雷', icon: 'el-icon-help', isHot: true, description: '经典扫雷游戏，考验你的推理能力' },
  { type: 'snake', name: '贪吃蛇', icon: 'el-icon-s-operation', isHot: true, description: '经典贪吃蛇，挑战你的反应速度' },
  { type: 'airplane', name: '打飞机', icon: 'el-icon-monitor', isHot: false, description: '经典打飞机游戏，重温童年记忆' },
  { type: '2048', name: '2048', icon: 'el-icon-collection-tag', isHot: true, description: '热门数字游戏，合并数字挑战高分' }
])

// 模拟热门文章数据
const hotArticles = ref([
  { id: 1, title: '扫雷游戏高级技巧分享', author: '游戏达人', excerpt: '分享扫雷游戏的高级技巧，帮助你成为扫雷大师', viewCount: 1234, likeCount: 56, createdAt: new Date() },
  { id: 2, title: '贪吃蛇AI算法实现', author: '技术大牛', excerpt: '使用AI算法实现贪吃蛇自动寻路，让电脑帮你玩游戏', viewCount: 892, likeCount: 34, createdAt: new Date(Date.now() - 3600000) },
  { id: 3, title: '2048游戏世界纪录挑战', author: '世界纪录保持者', excerpt: '挑战2048游戏的世界纪录，看看你能得多少分', viewCount: 1567, likeCount: 89, createdAt: new Date(Date.now() - 7200000) }
])

// 方法
const selectMode = (mode) => {
  modeStore.switchMode(mode)
  modeStore.setFirstEnter(false)
  showModeDialog.value = false
}

const handlePlayGame = (gameType) => {
  router.push(`/games/${gameType}`)
}

const handleViewArticle = (articleId) => {
  router.push(`/forum/article/${articleId}`)
}

const goToForum = () => {
  router.push('/forum')
}

const checkLocalServices = async () => {
  if (currentMode.value === 'local') {
    // 调用后端API检查本地服务状态
    try {
      // 这里应该调用后端API，暂时模拟
      localServices.value = {
        database: 'running',
        redis: 'running',
        nginx: 'running',
        backend: 'running'
      }
    } catch (error) {
      console.error('检查本地服务状态失败:', error)
    }
  }
}

// 生命周期钩子
onMounted(() => {
  // 检查本地服务状态
  checkLocalServices()
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 模式切换提示 */
.mode-alert {
  margin-bottom: 20px;
}

/* 模式选择弹窗 */
.mode-selection {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.mode-item {
  padding: 20px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  background-color: #fff;
}

.mode-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: var(--primary-color);
}

.mode-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 15px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.mode-icon.online {
  background-color: var(--primary-color);
}

.mode-icon.local {
  background-color: var(--success-color);
}

.mode-icon.offline {
  background-color: var(--warning-color);
}

/* 页面标题 */
.page-title {
  font-size: 32px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 40px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

/* 区块标题 */
.section-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 30px;
  position: relative;
  padding-bottom: 10px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  border-radius: 2px;
}

/* 游戏网格 */
.games-section {
  margin-bottom: 40px;
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

/* 论坛区块 */
.forum-section {
  margin-bottom: 40px;
}

.article-carousel {
  margin-bottom: 20px;
}

.carousel-item {
  background-color: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.carousel-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.article-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
  line-height: 1.4;
}

.article-excerpt {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 20px;
  flex: 1;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
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

.view-more-btn {
  display: block;
  margin: 0 auto;
}

/* 本地部署状态 */
.local-status-section {
  margin-top: 40px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .mode-selection {
    grid-template-columns: 1fr;
  }
  
  .games-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .section-title {
    font-size: 20px;
  }
}
</style>
