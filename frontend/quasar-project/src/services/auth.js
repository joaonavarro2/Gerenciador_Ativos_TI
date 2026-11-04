const TOKEN_KEY = 'auth_token'
const USER_KEY = 'auth_user'

function storageFor (persistir) {
  return persistir ? localStorage : sessionStorage
}

export function salvarSessao (resposta, persistir) {
  clearSession()
  const storage = storageFor(persistir)
  storage.setItem(TOKEN_KEY, resposta.token)
  storage.setItem(USER_KEY, JSON.stringify(resposta.usuario))
}

export function usuarioAtual () {
  const value = localStorage.getItem(USER_KEY) || sessionStorage.getItem(USER_KEY)
  return value ? JSON.parse(value) : null
}

export function hasSession () {
  return Boolean(localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY))
}

export function clearSession () {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(USER_KEY)
}

const PAPEIS_ESCRITA = ['ADMINISTRADOR', 'ADMINISTRADOR GERAL', 'ADMINISTRADOR_GERAL']

function papelAtual () {
  return String(usuarioAtual()?.funcao || '').trim().toUpperCase()
}

/** Apenas administradores podem criar, editar e excluir registros. */
export function podeEditar () {
  return PAPEIS_ESCRITA.includes(papelAtual())
}

export function ehAdministradorGeral () {
  return ['ADMINISTRADOR GERAL', 'ADMINISTRADOR_GERAL'].includes(papelAtual())
}

export function tokenAtual () {
  return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY)
}

export function atualizarToken (resposta) {
  const storage = localStorage.getItem(TOKEN_KEY) ? localStorage : sessionStorage
  storage.setItem(TOKEN_KEY, resposta.token)
  storage.setItem(USER_KEY, JSON.stringify(resposta.usuario))
}

/** Segundos até o token expirar (lê o claim "exp" sem validar a assinatura). */
export function segundosParaExpirar (token) {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const { exp } = JSON.parse(atob(payload))
    return exp - Math.floor(Date.now() / 1000)
  } catch {
    return -1
  }
}

export function atualizarUsuario (usuario) {
  const storage = localStorage.getItem(USER_KEY) ? localStorage : sessionStorage
  storage.setItem(USER_KEY, JSON.stringify(usuario))
}
