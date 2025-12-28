import request from '@/utils/request'

// 登录
export function login(username, password) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

// 注册
export function register(data) {
  return request({
    url: '/api/user/register',
    method: 'post',
    data: {
      username: data.username,
      password: data.password,
      email: data.email
    }
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/api/user/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(info) {
  return request({
    url: '/api/user/info',
    method: 'put',
    data: info
  })
}

// 上传头像
export function uploadAvatar(formData) {
  return request({
    url: '/api/user/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 修改密码
export function changePassword(oldPassword, newPassword) {
  return request({
    url: '/api/user/password',
    method: 'put',
    data: {
      oldPassword,
      newPassword
    }
  })
}