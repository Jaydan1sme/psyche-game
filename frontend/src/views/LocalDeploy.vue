<template>
  <div class="local-deploy-container">
    <h2>本地部署配置</h2>
    
    <!-- 服务状态 -->
    <el-card class="status-card">
      <template #header>
        <div class="card-header">
          <h3>服务状态</h3>
        </div>
      </template>
      <div class="status-grid">
        <div class="status-item">
          <el-icon class="status-icon"><hard-drive /></el-icon>
          <div class="status-info">
            <h4>数据库</h4>
            <el-tag :type="services.database === 'running' ? 'success' : 'danger'">
              {{ services.database === 'running' ? '运行中' : '已停止' }}
            </el-tag>
          </div>
        </div>
        <div class="status-item">
          <el-icon class="status-icon"><collection /></el-icon>
          <div class="status-info">
            <h4>Redis</h4>
            <el-tag :type="services.redis === 'running' ? 'success' : 'danger'">
              {{ services.redis === 'running' ? '运行中' : '已停止' }}
            </el-tag>
          </div>
        </div>
        <div class="status-item">
          <el-icon class="status-icon"><connection /></el-icon>
          <div class="status-info">
            <h4>Nginx</h4>
            <el-tag :type="services.nginx === 'running' ? 'success' : 'danger'">
              {{ services.nginx === 'running' ? '运行中' : '已停止' }}
            </el-tag>
          </div>
        </div>
        <div class="status-item">
          <el-icon class="status-icon"><cpu /></el-icon>
          <div class="status-info">
            <h4>后端服务</h4>
            <el-tag :type="services.backend === 'running' ? 'success' : 'danger'">
              {{ services.backend === 'running' ? '运行中' : '已停止' }}
            </el-tag>
          </div>
        </div>
      </div>
      
      <div class="status-actions">
        <el-button type="primary" @click="checkServices">
          <el-icon><RefreshRight /></el-icon> 刷新状态
        </el-button>
      </div>
    </el-card>
    
    <!-- 模式切换 -->
    <el-card class="mode-card">
      <template #header>
        <div class="card-header">
          <h3>运行模式</h3>
        </div>
      </template>
      <div class="mode-switcher">
        <el-radio-group v-model="currentMode" @change="handleModeChange">
          <el-radio-button label="online">在线模式</el-radio-button>
          <el-radio-button label="local">本地模式</el-radio-button>
          <el-radio-button label="offline">离线模式</el-radio-button>
        </el-radio-group>
        <p class="mode-description">{{ modeDescription }}</p>
      </div>
    </el-card>
    
    <!-- 数据管理 -->
    <el-card class="data-card">
      <template #header>
        <div class="card-header">
          <h3>数据管理</h3>
        </div>
      </template>
      <div class="data-actions">
        <el-button type="success" @click="handleBackup">
          <el-icon><Download /></el-icon> 备份数据
        </el-button>
        <el-button type="warning" @click="handleRestore">
          <el-icon><Upload /></el-icon> 恢复数据
        </el-button>
        <el-button type="danger" @click="handleClear">
          <el-icon><Delete /></el-icon> 清空数据
        </el-button>
      </div>
      
      <!-- 数据统计 -->
      <div class="data-stats">
        <div class="stat-item">
          <h4>用户数量</h4>
          <div class="stat-value">{{ dataStats.userCount }}</div>
        </div>
        <div class="stat-item">
          <h4>游戏分数记录</h4>
          <div class="stat-value">{{ dataStats.gameScoreCount }}</div>
        </div>
        <div class="stat-item">
          <h4>论坛文章</h4>
          <div class="stat-value">{{ dataStats.articleCount }}</div>
        </div>
        <div class="stat-item">
          <h4>评论数量</h4>
          <div class="stat-value">{{ dataStats.commentCount }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useModeStore } from '@/store/mode'
import { ElMessage } from 'element-plus'

const modeStore = useModeStore()

// 响应式数据
const services = ref({
  database: 'running',
  redis: 'running',
  nginx: 'running',
  backend: 'running'
})

const dataStats = ref({
  userCount: 0,
  gameScoreCount: 0,
  articleCount: 0,
  commentCount: 0
})

// 计算属性
const currentMode = computed({
  get: () => modeStore.currentMode,
  set: (value) => modeStore.switchMode(value)
})

const modeDescription = computed(() => {
  switch (currentMode.value) {
    case 'online':
      return '连接到远程服务器，数据同步到云端，支持实时多人游戏'
    case 'local':
      return '使用本地部署的服务，数据存储在本地，无需网络连接'
    case 'offline':
      return '纯本地运行，数据存储在浏览器，无需任何网络连接'
    default:
      return ''
  }
})

// 方法
const checkServices = () => {
  // 调用后端API检查服务状态
  console.log('检查服务状态')
  // 模拟服务状态更新
  setTimeout(() => {
    services.value = {
      database: 'running',
      redis: 'running',
      nginx: 'running',
      backend: 'running'
    }
    ElMessage.success('服务状态已刷新')
  }, 500)
}

const handleModeChange = (mode) => {
  modeStore.switchMode(mode)
  ElMessage.success(`已切换到${mode === 'online' ? '在线' : mode === 'local' ? '本地' : '离线'}模式`)
}

const handleBackup = () => {
  // 调用后端API进行数据备份
  console.log('备份数据')
  ElMessage.success('数据备份已开始')
}

const handleRestore = () => {
  // 调用后端API进行数据恢复
  console.log('恢复数据')
  ElMessage.success('数据恢复已开始')
}

const handleClear = () => {
  // 调用后端API清空数据
  console.log('清空数据')
  ElMessage.success('数据清空已开始')
}
</script>

<style scoped>
.local-deploy-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.local-deploy-container h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 30px;
  position: relative;
  padding-bottom: 10px;
}

.local-deploy-container h2::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background-color: var(--primary-color);
  border-radius: 2px;
}

/* 卡片样式 */
.status-card,
.mode-card,
.data-card {
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

/* 服务状态 */
.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background-color: var(--bg-secondary);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.status-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.status-icon {
  font-size: 24px;
  color: var(--primary-color);
}

.status-info h4 {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.status-actions {
  text-align: right;
}

/* 模式切换 */
.mode-switcher {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px 0;
}

.mode-description {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

/* 数据管理 */
.data-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.data-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  padding: 16px;
  background-color: var(--bg-secondary);
  border-radius: 8px;
  text-align: center;
}

.stat-item h4 {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--primary-color);
}
</style>