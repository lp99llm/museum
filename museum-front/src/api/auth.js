import request from '@/utils/request'

export const authApi = {
  // 登录
  login(username, password) {
    return request.post('/auth/login', { username, password }).then(res => res.data)
  },
  // 注册
  register(data) {
    return request.post('/auth/register', data).then(res => res.data)
  },
  // 发送重置密码验证码
  sendResetCode(data) {
    return request.post('/auth/send-reset-code', data).then(res => res.data)
  },
  // 验证验证码
  verifyResetCode(data) {
    return request.post('/auth/verify-reset-code', data).then(res => res.data)
  },
  // 重置密码
  resetPassword(data) {
    return request.post('/auth/reset-password', data).then(res => res.data)
  }
}