import { boot } from 'quasar/wrappers'
import axios from 'axios'
import { clearSession } from '@/services/auth.js'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token') || sessionStorage.getItem('auth_token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && typeof window !== 'undefined') {
      const rotaAtual = window.location.hash.slice(1) || '/dashboard'
      clearSession()
      if (!rotaAtual.startsWith('/login')) {
        window.location.hash = `/login?redirect=${encodeURIComponent(rotaAtual)}`
      }
    }
    return Promise.reject(error)
  },
)

export default boot(({ app }) => {
  app.config.globalProperties.$api = api
})
