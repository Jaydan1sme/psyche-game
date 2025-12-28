<template>
  <div class="login-container" ref="containerRef">
    <!-- 粒子爆炸容器 -->
    <div class="particles-container" ref="particlesContainer"></div>
    
    <!-- 登录表单卡片 -->
    <div class="login-form-wrapper" ref="formWrapperRef">
      <h2 class="login-title">登录</h2>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
        class="login-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            autocomplete="off"
            class="light-input"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            class="light-input"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-btn" :loading="isLoading">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-link">
        还没有账号？<a href="/register">立即注册</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const containerRef = ref()
const formWrapperRef = ref()
const particlesContainer = ref()

// 响应式数据
const isLoading = ref(false)
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 输入框聚焦事件
const handleInputFocus = (event) => {
  const input = event.target.closest('.el-input__wrapper')
  if (input) {
    input.classList.add('focused')
  }
}

// 输入框失焦事件
const handleInputBlur = (event) => {
  const input = event.target.closest('.el-input__wrapper')
  if (input) {
    input.classList.remove('focused')
  }
}

// 创建粒子爆炸效果
const createParticleExplosion = () => {
  if (!particlesContainer.value) return
  
  const colors = ['#6366F1', '#8B5CF6', '#EC4899', '#F97316', '#10B981']
  const particleCount = 100
  
  for (let i = 0; i < particleCount; i++) {
    const particle = document.createElement('div')
    particle.className = 'particle'
    
    // 随机位置
    const x = Math.random() * particlesContainer.value.offsetWidth
    const y = Math.random() * particlesContainer.value.offsetHeight
    particle.style.left = `${x}px`
    particle.style.top = `${y}px`
    
    // 随机颜色
    particle.style.backgroundColor = colors[Math.floor(Math.random() * colors.length)]
    
    // 随机大小
    const size = Math.random() * 5 + 2
    particle.style.width = `${size}px`
    particle.style.height = `${size}px`
    
    // 随机方向和速度
    const angle = Math.random() * Math.PI * 2
    const speed = Math.random() * 3 + 1
    const vx = Math.cos(angle) * speed
    const vy = Math.sin(angle) * speed
    
    // 动画
    particle.animate([
      {
        transform: 'translate(0, 0) scale(1)',
        opacity: 1
      },
      {
        transform: `translate(${vx * 50}px, ${vy * 50}px) scale(0)`,
        opacity: 0
      }
    ], {
      duration: 1000 + Math.random() * 500,
      easing: 'cubic-bezier(0.25, 0.46, 0.45, 0.94)'
    })
    
    particlesContainer.value.appendChild(particle)
    
    // 动画结束后移除粒子
    setTimeout(() => {
      if (particle.parentElement) {
        particle.parentElement.removeChild(particle)
      }
    }, 1500)
  }
}

// 方法
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    isLoading.value = true
    
    // 调用登录API
    const result = await userStore.login(loginForm.username, loginForm.password)
    
    if (result) {
      // 触发粒子爆炸效果
      createParticleExplosion()
      
      ElMessage.success('登录成功')
      setTimeout(() => {
        router.push('/')
      }, 1000)
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请重试')
  } finally {
    isLoading.value = false
  }
}

// 视差滚动效果
const handleScroll = () => {
  if (!formWrapperRef.value) return
  
  const scrollY = window.scrollY
  const parallaxOffset = scrollY * 0.1
  formWrapperRef.value.style.transform = `translateY(${parallaxOffset}px)`
}

// 生命周期钩子
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* 登录容器 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  position: relative;
  overflow: hidden;
  background-color: var(--bg-secondary);
}

/* 粒子容器 */
.particles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

/* 粒子样式 */
.particle {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  z-index: 1;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
}

/* 登录表单包装器 */
.login-form-wrapper {
  width: 420px;
  padding: 50px;
  background: var(--primary-bg);
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-color);
  position: relative;
  z-index: 2;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-form-wrapper:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

/* 登录标题 */
.login-title {
  font-size: 32px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 40px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 登录表单 */
.login-form {
  margin-bottom: 30px;
}

/* 表单标签 */
.el-form-item__label {
  color: var(--text-primary);
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 8px;
}

/* 浅色输入框 */
.light-input .el-input__wrapper {
  background-color: var(--bg-secondary);
  border: none;
  border-radius: 8px;
  border-bottom: 2px solid transparent;
  box-shadow: none;
  transition: all 0.3s ease;
}

.light-input .el-input__wrapper:focus {
  background-color: var(--primary-bg);
  border-bottom: 2px solid var(--gradient-start);
  box-shadow: none;
  animation: none;
}

/* 输入框文本颜色 */
.light-input .el-input__inner {
  color: var(--text-primary);
  background: transparent;
  border: none;
}

.light-input .el-input__inner::placeholder {
  color: var(--text-tertiary);
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  border: none;
  border-radius: var(--border-radius);
  color: var(--text-light);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: var(--shadow-light);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
  background: linear-gradient(120deg, var(--gradient-end), var(--gradient-start));
}

.login-btn:active {
  transform: translateY(0);
  box-shadow: var(--shadow-light);
}

.login-btn:disabled {
  opacity: 0.6;
  transform: none;
  box-shadow: none;
}

/* 注册链接 */
.register-link {
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
}

.register-link a {
  color: var(--accent-color);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.register-link a:hover {
  color: var(--gradient-end);
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-form-wrapper {
    width: 100%;
    max-width: 380px;
    padding: 40px 30px;
  }
  
  .login-title {
    font-size: 28px;
    margin-bottom: 30px;
  }
}
</style>