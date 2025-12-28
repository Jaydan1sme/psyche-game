/**
 * 日期时间工具模块
 */

/**
 * 格式化时间
 * @param {Date|string|number} date - 日期对象、字符串或时间戳
 * @param {string} format - 格式化字符串，如 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  
  // 转换为Date对象
  const d = new Date(date)
  
  // 检查是否为有效日期
  if (isNaN(d.getTime())) return ''
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化相对时间
 * @param {Date|string|number} date - 日期对象、字符串或时间戳
 * @returns {string} 相对时间字符串，如 '刚刚'、'5分钟前'、'1小时前'、'昨天'等
 */
export function formatRelativeTime(date) {
  if (!date) return ''
  
  const d = new Date(date)
  const now = new Date()
  const diff = now - d
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day
  const year = 365 * day
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < week) {
    return `${Math.floor(diff / day)}天前`
  } else if (diff < month) {
    return `${Math.floor(diff / week)}周前`
  } else if (diff < year) {
    return `${Math.floor(diff / month)}个月前`
  } else {
    return `${Math.floor(diff / year)}年前`
  }
}

/**
 * 获取当前时间戳
 * @returns {number} 当前时间戳（毫秒）
 */
export function getCurrentTimestamp() {
  return Date.now()
}

/**
 * 将时间戳转换为Date对象
 * @param {number} timestamp - 时间戳（毫秒）
 * @returns {Date} Date对象
 */
export function timestampToDate(timestamp) {
  return new Date(timestamp)
}

/**
 * 判断是否为今天
 * @param {Date|string|number} date - 日期对象、字符串或时间戳
 * @returns {boolean} 是否为今天
 */
export function isToday(date) {
  const d = new Date(date)
  const today = new Date()
  return d.toDateString() === today.toDateString()
}

/**
 * 判断是否为昨天
 * @param {Date|string|number} date - 日期对象、字符串或时间戳
 * @returns {boolean} 是否为昨天
 */
export function isYesterday(date) {
  const d = new Date(date)
  const yesterday = new Date()
  yesterday.setDate(yesterday.getDate() - 1)
  return d.toDateString() === yesterday.toDateString()
}