import { defineStore } from 'pinia'

export const useModeStore = defineStore('mode', {
  state: () => ({
    // 模式：local（本地部署）、online（在线模式）、offline（离线模式，非本地部署）
    currentMode: localStorage.getItem('currentMode') || 'online',
    // API基础地址
    apiBaseUrl: localStorage.getItem('apiBaseUrl') || 'http://localhost:8080',
    // WebSocket基础地址
    wsBaseUrl: localStorage.getItem('wsBaseUrl') || 'ws://localhost:8081',
    // 本地服务状态
    localServices: {
      database: 'stopped', // running/stopped/error
      redis: 'stopped',
      nginx: 'stopped',
      backend: 'stopped'
    },
    // 首次进入标记
    isFirstEnter: localStorage.getItem('isFirstEnter') !== 'false'
  }),
  
  getters: {
    // 是否为本地模式
    isLocalMode: (state) => state.currentMode === 'local',
    // 是否为在线模式
    isOnlineMode: (state) => state.currentMode === 'online',
    // 是否为离线模式
    isOfflineMode: (state) => state.currentMode === 'offline',
    // 是否为非本地部署的离线模式
    isPureOfflineMode: (state) => state.currentMode === 'offline',
    // API完整地址
    fullApiUrl: (state) => (path) => `${state.apiBaseUrl}${path}`,
    // WebSocket完整地址
    fullWsUrl: (state) => (path) => `${state.wsBaseUrl}${path}`
  },
  
  actions: {
    // 切换模式
    async switchMode(mode, apiUrl = '', wsUrl = '') {
      this.currentMode = mode
      if (apiUrl) {
        this.apiBaseUrl = apiUrl
      } else {
        this.apiBaseUrl = mode === 'local' ? 'http://localhost:8080' : 'https://api.psychegame.com'
      }
      
      if (wsUrl) {
        this.wsBaseUrl = wsUrl
      } else {
        this.wsBaseUrl = mode === 'local' ? 'ws://localhost:8081' : 'wss://api.psychegame.com'
      }
      
      // 保存到本地存储
      localStorage.setItem('currentMode', this.currentMode)
      localStorage.setItem('apiBaseUrl', this.apiBaseUrl)
      localStorage.setItem('wsBaseUrl', this.wsBaseUrl)
      
      // 模式切换后需要重新登录
      if (mode !== this.currentMode) {
        localStorage.removeItem('token')
        location.reload()
      }
    },
    
    // 更新本地服务状态
    updateLocalServices(status) {
      this.localServices = { ...this.localServices, ...status }
    },
    
    // 设置首次进入标记
    setFirstEnter(flag) {
      this.isFirstEnter = flag
      localStorage.setItem('isFirstEnter', String(flag))
    },
    
    // 检查本地服务状态
    async checkLocalServices() {
      try {
        // 这里应该调用后端API检查本地服务状态
        // 暂时模拟实现
        this.localServices = {
          database: 'running',
          redis: 'running',
          nginx: 'running',
          backend: 'running'
        }
        return this.localServices
      } catch (error) {
        console.error('检查本地服务状态失败:', error)
        return this.localServices
      }
    }
  }
})
