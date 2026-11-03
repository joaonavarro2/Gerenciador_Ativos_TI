import { boot } from 'quasar/wrappers'
import axios from 'axios'
import { Notify } from 'quasar'
import {
  atualizarToken,
  clearSession,
  ehAdministradorGeral,
  podeEditar,
  segundosParaExpirar,
  tokenAtual,
} from '@/services/auth.js'

const baseURL = import.meta.env.VITE_API_URL || '/api'
const RENOVAR_QUANDO_FALTAREM_SEGUNDOS = 5 * 60

export const api = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
})

let renovacaoEmAndamento = null

// Renova o token quando estiver perto de expirar, para o usuário ativo não ser deslogado.
async function renovarSeNecessario (token) {
  const restante = segundosParaExpirar(token)
  if (restante <= 0 || restante > RENOVAR_QUANDO_FALTAREM_SEGUNDOS) return token

  if (!renovacaoEmAndamento) {
    renovacaoEmAndamento = axios
      .post(`${baseURL}/auth/renovar`, null, { headers: { Authorization: `Bearer ${token}` } })
      .then(({ data }) => {
        atualizarToken(data)
        return data.token
      })
      .catch(() => token)
      .finally(() => { renovacaoEmAndamento = null })
  }
  return renovacaoEmAndamento
}

api.interceptors.request.use(async (config) => {
  if (typeof FormData !== 'undefined' && config.data instanceof FormData) {
    config.headers.delete?.('Content-Type')
    config.headers.delete?.('content-type')
  }

  let token = tokenAtual()

  if (token) {
    token = await renovarSeNecessario(token)
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    if (status === 401 && typeof window !== 'undefined') {
      const rotaAtual = window.location.hash.slice(1) || '/dashboard'
      clearSession()
      if (!rotaAtual.startsWith('/login')) {
        window.location.hash = `/login?redirect=${encodeURIComponent(rotaAtual)}`
      }
    } else if (status === 403) {
      Notify.create({ type: 'warning', message: 'Você não tem permissão para realizar esta ação.' })
    }
    return Promise.reject(error)
  },
)

export default boot(({ app }) => {
  app.config.globalProperties.$api = api
  app.config.globalProperties.$podeEditar = podeEditar
  app.config.globalProperties.$ehAdministradorGeral = ehAdministradorGeral
})
