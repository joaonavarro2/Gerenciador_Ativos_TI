// Runtime validation coverage for: session permissions and role visibility.
import { beforeEach, describe, expect, it } from 'vitest'
import {
  clearSession,
  ehAdministradorGeral,
  podeEditar,
  salvarSessao,
  segundosParaExpirar,
  atualizarToken,
  usuarioAtual,
} from './auth.js'

describe('permissoes de sessao', () => {
  beforeEach(() => {
    clearSession()
  })

  it('permite operacoes gerais aos dois perfis administrativos', () => {
    for (const funcao of ['ADMINISTRADOR', 'ADMINISTRADOR GERAL']) {
      salvarSessao({ token: 'token', usuario: { funcao } }, false)

      expect(podeEditar()).toBe(true)
      expect(ehAdministradorGeral()).toBe(funcao === 'ADMINISTRADOR GERAL')
    }
  })

  it('nao concede permissao de escrita a uma funcao legada ou desconhecida', () => {
    salvarSessao({ token: 'token', usuario: { funcao: 'USUARIO' } }, false)

    expect(podeEditar()).toBe(false)
    expect(ehAdministradorGeral()).toBe(false)
  })

  it('persiste a escolha lembrar-me no armazenamento correto', () => {
    salvarSessao({ token: 'token', usuario: { funcao: 'ADMINISTRADOR' } }, true)

    expect(localStorage.getItem('auth_token')).toBe('token')
    expect(sessionStorage.getItem('auth_token')).toBeNull()
    expect(usuarioAtual().funcao).toBe('ADMINISTRADOR')
  })

  it('renova o token no mesmo armazenamento escolhido para a sessao', () => {
    salvarSessao({ token: 'token-antigo', usuario: { funcao: 'ADMINISTRADOR' } }, true)
    atualizarToken({ token: 'token-renovado-local', usuario: { funcao: 'ADMINISTRADOR' } })

    expect(localStorage.getItem('auth_token')).toBe('token-renovado-local')
    expect(sessionStorage.getItem('auth_token')).toBeNull()

    salvarSessao({ token: 'token-antigo', usuario: { funcao: 'ADMINISTRADOR' } }, false)
    atualizarToken({ token: 'token-renovado', usuario: { funcao: 'ADMINISTRADOR' } })

    expect(sessionStorage.getItem('auth_token')).toBe('token-renovado')
    expect(localStorage.getItem('auth_token')).toBeNull()
  })

  it('calcula a expiracao de JWT e sinaliza tokens malformados', () => {
    const expiracao = Math.floor(Date.now() / 1000) + 3600
    const payload = btoa(JSON.stringify({ exp: expiracao }))
      .replace(/=/g, '')
      .replace(/\+/g, '-')
      .replace(/\//g, '_')

    expect(segundosParaExpirar(`header.${payload}.signature`)).toBeGreaterThan(3598)
    expect(segundosParaExpirar('token-invalido')).toBe(-1)
  })
})
