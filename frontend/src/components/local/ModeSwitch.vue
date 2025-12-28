<template>
  <div class="mode-switch-container">
    <el-dropdown trigger="hover" @command="handleModeChange">
      <div class="current-mode" :class="currentMode">
        <i class="el-icon-setting"></i>
        <span>{{ modeLabel }}</span>
        <i class="el-icon-arrow-down el-icon--right"></i>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="online">
            <div class="mode-item online">
              <i class="el-icon-video-camera"></i>
              <span>在线模式</span>
            </div>
          </el-dropdown-item>
          <el-dropdown-item command="local">
            <div class="mode-item local">
              <i class="el-icon-document"></i>
              <span>本地模式</span>
            </div>
          </el-dropdown-item>
          <el-dropdown-item command="offline">
            <div class="mode-item offline">
              <i class="el-icon-close"></i>
              <span>离线模式</span>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    
    <!-- 模式切换确认对话框 -->
    <el-dialog
      v-model="showConfirmDialog"
      title="模式切换确认"
      width="400px"
      :close-on-click-modal="false"
    >
      <p>切换到{{ newModeLabel }}将{{ modeSwitchTips }}，确定要继续吗？</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showConfirmDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmModeChange">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useModeStore } from '@/store/mode'
import { useUserStore } from '@/store/user'

const modeStore = useModeStore()
const userStore = useUserStore()

// 当前模式
const currentMode = computed(() => modeStore.currentMode)

// 模式标签
const modeLabel = computed(() => {
  const labels = {
    online: '在线模式',
    local: '本地模式',
    offline: '离线模式'
  }
  return labels[currentMode.value] || '在线模式'
})

// 对话框相关
const showConfirmDialog = ref(false)
const pendingMode = ref('')

// 新模式标签
const newModeLabel = computed(() => {
  const labels = {
    online: '在线模式',
    local: '本地模式',
    offline: '离线模式'
  }
  return labels[pendingMode.value] || ''
})

// 模式切换提示
const modeSwitchTips = computed(() => {
  const tips = {
    online: '连接到远程服务器',
    local: '使用本地部署的服务',
    offline: '断开与服务器的连接，数据将存储在本地'
  }
  return tips[pendingMode.value] || ''
})

// 处理模式变化
const handleModeChange = (mode) => {
  if (mode === currentMode.value) return
  
  pendingMode.value = mode
  showConfirmDialog.value = true
}

// 确认模式切换
const confirmModeChange = async () => {
  try {
    await modeStore.switchMode(pendingMode.value)
    // 模式切换后需要重新登录
    userStore.logout()
    showConfirmDialog.value = false
  } catch (error) {
    console.error('模式切换失败:', error)
  }
}
</script>

<style scoped>
.mode-switch-container {
  position: relative;
}

.current-mode {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.current-mode:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 在线模式样式 */
.current-mode.online {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
}

.current-mode.online:hover {
  background-color: #bae7ff;
}

/* 本地模式样式 */
.current-mode.local {
  background-color: #f6ffed;
  color: #52c41a;
  border-color: #b7eb8f;
}

.current-mode.local:hover {
  background-color: #d9f7be;
}

/* 离线模式样式 */
.current-mode.offline {
  background-color: #f5f5f5;
  color: #8c8c8c;
  border-color: #d9d9d9;
}

.current-mode.offline:hover {
  background-color: #e8e8e8;
}

/* 下拉菜单中的模式项 */
.mode-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
}

.mode-item.online {
  color: #1890ff;
}

.mode-item.local {
  color: #52c41a;
}

.mode-item.offline {
  color: #8c8c8c;
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
