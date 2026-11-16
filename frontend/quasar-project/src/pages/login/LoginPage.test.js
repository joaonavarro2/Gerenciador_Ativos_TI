// Runtime validation coverage for: login submission and credential errors.
import { mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import LoginPage from './LoginPage.vue'

const { apiPost, notify, push, saveSession } = vi.hoisted(() => ({
  apiPost: vi.fn(),
  notify: vi.fn(),
  push: vi.fn(),
  saveSession: vi.fn(),
}))

vi.mock('@/boot/axios.js', () => ({
  api: { post: apiPost },
}))

vi.mock('@/services/auth.js', () => ({
  salvarSessao: saveSession,
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push,
    currentRoute: { value: { query: {} } },
  }),
}))

vi.mock('quasar', () => ({
  useQuasar: () => ({ notify }),
}))

const InputStub = {
  props: ['modelValue', 'type', 'placeholder', 'error', 'errorMessage'],
  emits: ['update:modelValue'],
  template: `
    <label>
      <input
        :type="type"
        :placeholder="placeholder"
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
      >
      <span v-if="error">{{ errorMessage }}</span>
      <slot name="append" />
    </label>
  `,
}

const stubs = {
  'q-form': {
    emits: ['submit'],
    template: `<form @submit="$emit('submit', $event)"><slot /></form>`,
  },
  'q-input': InputStub,
  'q-btn': {
    props: ['label', 'type', 'loading', 'ariaLabel'],
    emits: ['click'],
    template: `<button :type="type || 'button'" @click="$emit('click')">{{ label }}<slot /></button>`,
  },
  'q-icon': true,
  'q-checkbox': true,
}

describe('login', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('envia as credenciais, salva a sessao e navega apos autenticacao', async () => {
    const resposta = {
      token: 'token-de-teste',
      usuario: { funcao: 'ADMINISTRADOR' },
    }
    apiPost.mockResolvedValue({ data: resposta })
    const wrapper = mount(LoginPage, { global: { stubs } })

    await wrapper.get('input[placeholder="E-mail"]').setValue('admin@example.test')
    await wrapper.get('input[placeholder="Senha"]').setValue('senha-de-teste')
    await wrapper.get('form').trigger('submit')

    expect(apiPost).toHaveBeenCalledWith('/auth/login', {
      email: 'admin@example.test',
      senha: 'senha-de-teste',
    })
    expect(saveSession).toHaveBeenCalledWith(resposta, false)
    expect(push).toHaveBeenCalledWith('/dashboard')
    wrapper.unmount()
  })

  it('mostra erro para credenciais invalidas sem persistir sessao', async () => {
    apiPost.mockRejectedValue({ response: { status: 401 } })
    const wrapper = mount(LoginPage, { global: { stubs } })

    await wrapper.get('input[placeholder="E-mail"]').setValue('admin@example.test')
    await wrapper.get('input[placeholder="Senha"]').setValue('senha-incorreta')
    await wrapper.get('form').trigger('submit')

    expect(await wrapper.find('span').text()).toBe('E-mail ou senha inválidos.')
    expect(saveSession).not.toHaveBeenCalled()
    expect(push).not.toHaveBeenCalled()
    wrapper.unmount()
  })
})
