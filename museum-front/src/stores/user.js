import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/api/auth'
import { clearPermissions } from '@/utils/permission'

const roleLabelMap = {
  ROLE_ADMIN: '系统管理员',
  ROLE_ARTIFACT_ADMIN: '文物管理员',
  ROLE_EXHIBITION_ADMIN: '展览管理员',
  ROLE_RESTORATION_STAFF: '修复人员',
  ROLE_PROCESS_ADMIN: '流程管理员'
}

const mapRoleCodesToLabel = (roleCodes = '') => {
  if (!roleCodes) return '未分配角色'
  return roleCodes
    .split(',')
    .map((code) => roleLabelMap[code] || code)
    .join('、')
}

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

  const login = async (username, password) => {
    const res = await authApi.login(username, password)
    setToken(res.token)
    setUser({
      username: res.username,
      role: mapRoleCodesToLabel(res.role),
      roleCode: res.role
    })
    return res
  }

  const logout = () => {
    token.value = ''
    user.value = null
    clearPermissions()
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, login, logout }
})
