import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUser = (userInfo) => {
    user.value = userInfo
    localStorage.setItem('user', JSON.stringify(userInfo))
  }

  const login = async (loginData) => {
    const res = await loginApi(loginData)
    setToken(res.token)
    setUser({ username: res.username, realName: res.realName })
    return res
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, login, logout }
})