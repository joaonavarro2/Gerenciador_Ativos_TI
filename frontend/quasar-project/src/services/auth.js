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
