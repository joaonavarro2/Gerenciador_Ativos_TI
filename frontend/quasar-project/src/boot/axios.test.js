import { beforeEach, describe, expect, it, vi } from 'vitest'

const mocks = vi.hoisted(() => ({
  api: null,
  requestInterceptor: null,
  responseErrorInterceptor: null,
  token: 'token-atual',
  segundos: 3600,
  post: vi.fn(),
  atualizarToken: vi.fn(),
  atualizarUsuario: vi.fn(),
  clearSession: vi.fn(),
  notify: vi.fn(),
}))

vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => {
      mocks.api = {
        interceptors: {
          request: {
            use: vi.fn((handler) => {
              mocks.requestInterceptor = handler
            }),
          },
          response: {
            use: vi.fn((resolve, reject) => {
              mocks.responseErrorInterceptor = reject
            }),
          },
        },
      }
      return mocks.api
    }),
    post: mocks.post,
  },
}))

vi.mock('quasar/wrappers', () => ({ boot: (callback) => callback }))
vi.mock('quasar', () => ({ Notify: { create: mocks.notify } }))
vi.mock('@/services/auth.js', () => ({
  atualizarToken: mocks.atualizarToken,
  atualizarUsuario: mocks.atualizarUsuario,
  clearSession: mocks.clearSession,
  ehAdministradorGeral: vi.fn(),
  podeEditar: vi.fn(),
  segundosParaExpirar: vi.fn(() => mocks.segundos),
  tokenAtual: vi.fn(() => mocks.token),
}))

import './axios.js'

describe('interceptores de autenticacao da API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mocks.token = 'token-atual'
    mocks.segundos = 3600
    window.location.hash = '#/bens'
  })

  it('renova token prestes a expirar e preserva a sessao no armazenamento escolhido', async () => {
    mocks.segundos = 120
    mocks.post.mockResolvedValue({
      data: { token: 'token-renovado', usuario: { funcao: 'ADMINISTRADOR' } },
    })
    const config = { headers: {} }

    await mocks.requestInterceptor(config)

    expect(mocks.post).toHaveBeenCalledWith('/api/auth/renovar', null, {
      headers: { Authorization: 'Bearer token-atual' },
    })
    expect(mocks.atualizarToken).toHaveBeenCalledWith({
      token: 'token-renovado',
      usuario: { funcao: 'ADMINISTRADOR' },
    })
    expect(config.headers.Authorization).toBe('Bearer token-renovado')
  })

  it('remove a sessao quando a API responde 401', async () => {
    const erro = { response: { status: 401 } }

    await expect(mocks.responseErrorInterceptor(erro)).rejects.toBe(erro)

    expect(mocks.clearSession).toHaveBeenCalledOnce()
    expect(window.location.hash).toContain('/login?redirect=')
  })

  it('notifica acesso proibido sem apagar a sessao', async () => {
    const erro = { response: { status: 403 } }

    await expect(mocks.responseErrorInterceptor(erro)).rejects.toBe(erro)

    expect(mocks.notify).toHaveBeenCalledWith({
      type: 'warning',
      message: 'Você não tem permissão para realizar esta ação.',
    })
    expect(mocks.clearSession).not.toHaveBeenCalled()
  })

  it('nao tenta renovar um token que ja expirou', async () => {
    mocks.segundos = 0
    const config = { headers: {} }

    await mocks.requestInterceptor(config)

    expect(mocks.post).not.toHaveBeenCalled()
    expect(config.headers.Authorization).toBe('Bearer token-atual')
  })
})
