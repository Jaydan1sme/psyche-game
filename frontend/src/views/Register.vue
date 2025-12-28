<template>
  <div class="register-container">
    <!-- 浅色背景 -->
    <div class="light-background"></div>
    
    <!-- 注册表单卡片 -->
    <div class="register-form-wrapper">
      <h2 class="register-title">创建账号</h2>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-position="top"
        class="register-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            autocomplete="off"
            class="light-input"
            @focus="handleInputFocus"
            @blur="handleInputBlur"
          />
          <div v-if="fieldErrors.username" class="form-error">{{ fieldErrors.username }}</div>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            class="light-input"
            @focus="handleInputFocus"
            @blur="handleInputBlur"
          />
          <div v-if="fieldErrors.password" class="form-error">{{ fieldErrors.password }}</div>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
            class="light-input"
            @focus="handleInputFocus"
            @blur="handleInputBlur"
          />
          <div v-if="fieldErrors.confirmPassword" class="form-error">{{ fieldErrors.confirmPassword }}</div>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            type="email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            autocomplete="off"
            class="light-input"
            @focus="handleInputFocus"
            @blur="handleInputBlur"
          />
          <div v-if="fieldErrors.email" class="form-error">{{ fieldErrors.email }}</div>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister" 
            class="register-btn" 
            :loading="isLoading"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-link">
        已有账号？<a href="/login">立即登录</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref()

// 响应式数据
const isLoading = ref(false)
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

// 字段错误信息
const fieldErrors = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 6, max: 16, message: '用户名长度在 6 到 16 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '用户名只能包含字母和数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度在 8 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
}

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    fieldErrors.confirmPassword = '两次输入密码不一致'
    callback(new Error('两次输入密码不一致'))
  } else {
    fieldErrors.confirmPassword = ''
    callback()
  }
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

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  // 清空之前的错误信息
  Object.keys(fieldErrors).forEach(key => {
    fieldErrors[key] = ''
  })
  
  try {
    await registerFormRef.value.validate()
    isLoading.value = true
    
    // 调用注册API
    const result = await userStore.register(registerForm)
    
    if (result) {
      ElMessage.success('注册成功，请登录')
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      ElMessage.error('注册失败，请重试')
    }
  } catch (error) {
    console.error('注册失败:', error)
    if (error instanceof Error) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('注册失败，请重试')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* 浅色背景 */
.light-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: var(--secondary-bg);
  z-index: -1;
}

/* 注册容器 */
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  position: relative;
  background-color: var(--secondary-bg);
}

/* 注册表单包装器 */
.register-form-wrapper {
  width: 420px;
  padding: 40px;
  background-color: var(--primary-bg);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-light);
  transition: all var(--transition-normal) ease;
}

.register-form-wrapper:hover {
  box-shadow: var(--shadow-medium);
  transform: translateY(-2px);
}

/* 注册标题 */
.register-title {
  font-size: 24px;
  font-weight: var(--font-weight-semibold);
  text-align: center;
  margin-bottom: 32px;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 注册表单 */
.register-form {
  margin-bottom: 24px;
}

/* 表单标签 */
.el-form-item__label {
  color: var(--text-primary);
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

/* 浅色输入框 */
.light-input .el-input__wrapper {
  background-color: var(--secondary-bg);
  border: none;
  border-bottom: 2px solid transparent;
  border-radius: 0;
  box-shadow: none;
  transition: all var(--transition-normal) ease;
}

.light-input .el-input__wrapper:hover {
  background-color: var(--primary-bg);
}

.light-input .el-input__wrapper.is-focus,
.light-input .el-input__wrapper.focused {
  background-color: var(--primary-bg);
  border-bottom: 2px solid;
  border-image: var(--gradient-primary);
  border-image-slice: 1;
  box-shadow: none;
}

/* 输入框文本颜色 */
.light-input .el-input__inner {
  color: var(--text-primary);
  background: transparent;
  border: none;
  font-size: 18px; /* 放大至18px */
}

.light-input .el-input__inner::placeholder {
  color: var(--text-light);
  font-size: 18px; /* 占位符也放大至18px */
}

/* 表单错误信息 */
.form-error {
  margin-top: 4px;
  font-size: 12px;
  color: #E91E63; /* 浅粉文字 */
  background-color: rgba(233, 30, 99, 0.1); /* 浅粉背景 */
  padding: 4px 8px;
  border-radius: 4px;
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  height: 48px;
  font-size: 20px; /* 放大至20px */
  font-weight: var(--font-weight-medium);
  background: var(--gradient-primary);
  border: none;
  border-radius: var(--border-radius);
  color: white;
  transition: all var(--transition-normal) ease;
  box-shadow: var(--shadow-medium);
}

.register-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-hover), 0 0 15px rgba(122, 134, 232, 0.3);
}

.register-btn:active {
  transform: translateY(0);
  box-shadow: var(--shadow-medium);
}

.register-btn:disabled {
  opacity: 0.6;
  transform: none;
  box-shadow: none;
}

/* 登录链接 */
.login-link {
  text-align: center;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.login-link a {
  color: var(--accent-color);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-normal) ease;
}

.login-link a:hover {
  color: var(--accent-light);
  text-shadow: 0 0 4px rgba(122, 134, 232, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-form-wrapper {
    width: 100%;
    max-width: 380px;
    padding: 30px 24px;
  }
  
  .register-title {
    font-size: 22px;
    margin-bottom: 24px;
  }
}
</style>