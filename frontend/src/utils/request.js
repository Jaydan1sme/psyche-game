import axios from 'axios'
import { useUserStore } from '@/store/user'
import { useModeStore } from '@/store/mode'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    const modeStore = useModeStore()
    
    // 设置API基础地址
    config.baseURL = modeStore.apiBaseUrl
    
    // 添加token
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    
    // 处理离线模式（非本地部署）
    if (modeStore.isPureOfflineMode) {
      // 拦截请求，存储到localStorage
      storeOfflineRequest(config)
      // 返回一个模拟的成功响应
      return Promise.resolve({
        data: {
          code: 200,
          message: '请求已存储，将在联网后自动同步',
          data: {}
        }
      })
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的code不是200，视为错误
    if (res.code !== 200) {
      // 处理不同错误码
      switch (res.code) {
        case 401:
          // 未登录或登录过期
          const userStore = useUserStore()
          userStore.logout()
          break
        case 403:
          // 权限不足
          console.error('权限不足')
          break
        default:
          console.error(res.message || '请求失败')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return response
  },
  error => {
    console.error('响应错误:', error)
    
    // 处理网络错误
    if (error.message.includes('Network Error')) {
      const modeStore = useModeStore()
      if (modeStore.isOnlineMode) {
        console.error('网络连接失败，建议切换到离线模式')
      }
    }
    
    return Promise.reject(error)
  }
)

// 存储离线请求到localStorage
function storeOfflineRequest(config) {
  const offlineRequests = JSON.parse(localStorage.getItem('offlineRequests')) || []
  
  const requestData = {
    id: Date.now() + Math.random().toString(36).substr(2, 9),
    url: config.url,
    method: config.method,
    data: config.data || {},
    params: config.params || {},
    timestamp: new Date().toISOString()
  }
  
  offlineRequests.push(requestData)
  localStorage.setItem('offlineRequests', JSON.stringify(offlineRequests))
}

// 同步离线请求
async function syncOfflineRequests() {
  const offlineRequests = JSON.parse(localStorage.getItem('offlineRequests')) || []
  if (offlineRequests.length === 0) {
    return { success: true, synced: 0 }
  }
  
  const modeStore = useModeStore()
  if (modeStore.isPureOfflineMode) {
    return { success: false, message: '当前处于离线模式，无法同步' }
  }
  
  let syncedCount = 0
  const failedRequests = []
  
  for (const request of offlineRequests) {
    try {
      await service({
        url: request.url,
        method: request.method,
        data: request.data,
        params: request.params
      })
      syncedCount++
    } catch (error) {
      console.error(`同步请求失败: ${request.url}`, error)
      failedRequests.push(request)
    }
  }
  
  // 更新离线请求列表，只保留失败的请求
  localStorage.setItem('offlineRequests', JSON.stringify(failedRequests))
  
  return { success: true, synced: syncedCount, failed: failedRequests.length }
}

// 导出service实例和同步函数
export default service
export { syncOfflineRequests }
