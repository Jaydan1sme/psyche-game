import { defineStore } from 'pinia'
import { login as apiLogin, register as apiRegister, getUserInfo, updateUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    // 用户信息
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || {},
    // 登录状态
    isLoggedIn: localStorage.getItem('token') ? true : false,
    // JWT token
    token: localStorage.getItem('token') || ''
  }),
  
  getters: {
    // 获取用户ID
    userId: (state) => state.userInfo.id || null,
    // 获取用户名
    username: (state) => state.userInfo.username || '',
    // 获取用户昵称
    nickname: (state) => state.userInfo.nickname || state.userInfo.username || '',
    // 获取用户头像
    avatar: (state) => state.userInfo.avatar || '',
    // 获取用户角色
    role: (state) => state.userInfo.role || 'user'
  },
  
  actions: {
    // 登录
    async login(username, password) {
      try {
        const response = await apiLogin(username, password)
        const { token, userInfo } = response.data.data
        
        // 保存到状态
        this.token = token
        this.userInfo = userInfo
        this.isLoggedIn = true
        
        // 保存到本地存储
        localStorage.setItem('token', token)
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
        
        return true
      } catch (error) {
        console.error('登录失败:', error)
        throw error
      }
    },
    
    // 注册
    async register(data) {
      try {
        const response = await apiRegister(data)
        return response.data.code === 200
      } catch (error) {
        console.error('注册失败:', error)
        throw error
      }
    },
    
    // 登出
    logout() {
      // 清除状态
      this.token = ''
      this.userInfo = {}
      this.isLoggedIn = false
      
      // 清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      // 跳转到登录页
      window.location.href = '/login'
    },
    
    // 获取用户信息
    async fetchUserInfo() {
      try {
        const response = await getUserInfo()
        this.userInfo = response.data
        localStorage.setItem('userInfo', JSON.stringify(response.data))
        return response
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 如果获取失败，可能是token过期，需要登出
        this.logout()
        throw error
      }
    },
    
    // 更新用户信息
    async updateInfo(info) {
      try {
        const response = await updateUserInfo(info)
        this.userInfo = { ...this.userInfo, ...response.data }
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        return response
      } catch (error) {
        console.error('更新用户信息失败:', error)
        throw error
      }
    },
    
    // 设置头像
    setAvatar(avatarUrl) {
      this.userInfo.avatar = avatarUrl
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    }
  }
})
