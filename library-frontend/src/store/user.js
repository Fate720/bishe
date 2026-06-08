import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(sessionStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isAdmin = computed(() => {
    return sessionStorage.getItem('isAdmin') === 'true'
  })

  const username = computed(() => {
    return userInfo.value?.username || sessionStorage.getItem('username') || '用户'
  })

  function setToken(newToken) {
    token.value = newToken
    sessionStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
  }

  async function fetchUserInfo() {
    try {
      const res = await getCurrentUser()
      userInfo.value = res
      sessionStorage.setItem('username', res.username)
      sessionStorage.setItem('isAdmin', String(res.isAdmin))
      return res
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('isAdmin')
    sessionStorage.removeItem('username')
  }

  return {
    token,
    userInfo,
    isAdmin,
    username,
    setToken,
    setUserInfo,
    fetchUserInfo,
    logout
  }
})
