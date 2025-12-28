<template>
  <div class="local-status-container">
    <h3>本地部署状态</h3>
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
      <el-button type="primary" @click="handleRefresh">
        <el-icon><RefreshRight /></el-icon> 刷新状态
      </el-button>
      <el-button type="success" @click="handleBackup">
        <el-icon><Download /></el-icon> 备份数据
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const emit = defineEmits(['refresh'])

// 响应式数据
const services = ref({
  database: 'running',
  redis: 'running',
  nginx: 'running',
  backend: 'running'
})

// 方法
const handleRefresh = () => {
  emit('refresh')
}

const handleBackup = () => {
  // 调用后端API进行数据备份
  console.log('备份数据')
}

// 生命周期钩子
onMounted(() => {
  // 初始化状态
  handleRefresh()
})
</script>

<style scoped>
.local-status-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.local-status-container h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

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
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.status-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>