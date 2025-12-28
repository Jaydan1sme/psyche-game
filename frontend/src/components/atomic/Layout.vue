<template>
  <div class="layout-container">
    <!-- 头部导航 -->
    <header class="layout-header">
      <div class="header-left">
        <logo />
      </div>
      <div class="header-right">
        <!-- 模式切换按钮 -->
        <mode-switch class="mode-switch" />
        <!-- 用户信息 -->
        <div v-if="isLoggedIn" class="user-info-section">
          <el-dropdown trigger="click" @command="handleDropdownCommand">
            <div class="user-info">
              <el-avatar :src="avatar" size="small">{{ nickname ? nickname.charAt(0) : '' }}</el-avatar>
              <span class="username">{{ nickname }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="local-deploy">本地部署配置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div v-else class="user-login-section">
          <el-button type="primary" @click="goToLogin">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
        </div>
      </div>
    </header>
    
    <!-- 主内容区 -->
    <main class="layout-main">
      <!-- 侧边栏 -->
      <aside class="layout-sidebar">
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          router
          unique-opened
        >
          <el-menu-item index="/">
            <template #icon><house /></template>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/games">
            <template #icon><video-play /></template>
            <span>游戏列表</span>
          </el-menu-item>
          <el-menu-item index="/forum">
            <template #icon><chat-dot-round /></template>
            <span>论坛</span>
          </el-menu-item>
          <el-menu-item index="/profile">
            <template #icon><user /></template>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </aside>
      
      <!-- 内容区域 -->
      <section class="content-wrapper">
        <transition name="content-fade" mode="out-in">
          <router-view />
        </transition>
      </section>
    </main>
    
    <!-- 底部 -->
    <footer class="layout-footer">
      <div class="footer-content">
        <p>© 2025 Psyche Game. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import ModeSwitch from '@/components/local/ModeSwitch.vue'
import Logo from '@/components/common/Logo.vue'
import {
  House, VideoPlay, ChatDotRound, User, ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 计算当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 获取用户信息
const isLoggedIn = computed(() => userStore.isLoggedIn)
const nickname = computed(() => userStore.nickname || '游客')
const avatar = computed(() => userStore.avatar)

// 处理下拉菜单命令
const handleDropdownCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'local-deploy':
      router.push('/local-deploy')
      break
    case 'logout':
      userStore.logout()
      break
  }
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}

// 页面加载时获取用户信息
onMounted(async () => {
  if (userStore.isLoggedIn && !userStore.userInfo.id) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
})
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: var(--bg-secondary);
}

/* 头部样式 */
.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background: linear-gradient(180deg, rgba(245, 247, 250, 0.95), rgba(245, 247, 250, 0.8));
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s ease;
}

.layout-header.scrolled {
  background: var(--primary-bg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-right: auto;
}

.logo-container {
  width: 80px;
  height: 30px;
  margin: 0;
  padding: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--border-radius);
  transition: all 0.3s ease;
}

.user-info:hover {
  background-color: var(--bg-secondary);
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

/* 主内容区样式 */
.layout-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 侧边栏样式 */
.layout-sidebar {
  width: auto;
  max-width: 240px;
  background-color: var(--primary-bg);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  overflow-y: auto;
  transition: all 0.3s ease;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
  background-color: transparent;
}

.sidebar-menu .el-menu-item {
  height: 60px;
  line-height: 60px;
  padding: 0 20px;
  transition: all 0.3s ease;
  color: var(--text-primary);
  border-left: 3px solid transparent;
  font-size: var(--left-font-item);
  font-weight: 400;
  color: var(--text-primary);
}

/* 侧边栏标题样式（如果有） */
.sidebar-menu .el-sub-menu__title {
  font-size: var(--left-font-title);
  font-weight: 600;
  color: var(--text-primary);
  height: 70px;
  line-height: 70px;
  padding: 0 20px;
}

/* 侧边栏辅助文字样式 */
.sidebar-menu .assist-text {
  font-size: var(--left-font-assist);
  font-weight: 300;
  color: var(--text-tertiary);
}

/* 调整侧边栏图标与文字间距 */
.sidebar-menu .el-menu-item .el-icon {
  margin-right: 12px;
  font-size: 1.2em;
}

.sidebar-menu .el-sub-menu__title .el-icon {
  margin-right: 12px;
  font-size: 1.2em;
}

.sidebar-menu .el-menu-item:hover {
  background-color: var(--bg-secondary);
  color: var(--accent-color);
}

.sidebar-menu .el-menu-item.is-active {
  background-color: transparent;
  color: var(--accent-color);
  border-left-color: var(--accent-color);
  font-weight: 600;
}

.sidebar-menu .el-menu-item.is-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 2px;
  background: linear-gradient(90deg, var(--gradient-start), var(--gradient-end));
  border-radius: 1px;
}

/* 内容区域样式 */
.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: var(--bg-secondary);
}

/* 底部样式 */
.layout-footer {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--primary-bg);
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  font-size: 14px;
  color: var(--text-secondary);
}

/* 内容过渡动画 */
.content-fade-enter-active,
.content-fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.content-fade-enter-from,
.content-fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .layout-sidebar {
    width: 60px;
  }
  
  .sidebar-menu .el-menu-item span {
    display: none;
  }
  
  .sidebar-menu .el-menu-item {
    padding: 0;
    text-align: center;
  }
  
  .sidebar-menu .el-menu-item.is-active::after {
    left: 10px;
    right: 10px;
  }
}
</style>
