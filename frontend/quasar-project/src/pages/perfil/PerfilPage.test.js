import { flushPromises, mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import PerfilPage from './PerfilPage.vue'

const mocks = vi.hoisted(() => ({
  get: vi.fn(),
  put: vi.fn(),
  notify: vi.fn(),
  replace: vi.fn(),
  atualizarToken: vi.fn(),
  atualizarUsuario: vi.fn(),
  clearSession: vi.fn(),
}))

vi.mock('@/boot/axios.js', () => ({
  api: { get: mocks.get, put: mocks.put },
}))

vi.mock('@/services/auth.js', () => ({
  atualizarToken: mocks.atualizarToken,
  atualizarUsuario: mocks.atualizarUsuario,
  clearSession: mocks.clearSession,
}))

vi.mock('quasar', () => ({
  useQuasar: () => ({ notify: mocks.notify }),
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({ replace: mocks.replace }),
}))

const PassThrough = { template: '<div><slot /><slot name="avatar" /><slot name="action" /></div>' }
const InputStub = {
  props: ['modelValue', 'label', 'type', 'rules', 'maxlength', 'autocomplete'],
  emits: ['update:modelValue'],
  template: `
    <label>
      {{ label }}
      <input :type="type" :value="modelValue" @input="$emit('update:modelValue', $event.target.value)">
    </label>
  `,
}
const ButtonStub = {
  props: ['label', 'type', 'disable', 'loading'],
  emits: ['click'],
  template: `<button :type="type || 'button'" :disabled="disable || loading" @click="$emit('click')">{{ label }}<slot /></button>`,
}
const FormStub = {
  emits: ['submit'],
  template: `<form @submit="$emit('submit', $event)"><slot /></form>`,
}
const DialogStub = {
  props: ['modelValue'],
  template: '<div v-if="modelValue"><slot /></div>',
}
const stubs = {
  'q-page': PassThrough,
  'q-banner': PassThrough,
  'q-btn': ButtonStub,
  'q-card': PassThrough,
  'q-card-section': PassThrough,
  'q-card-actions': PassThrough,
  'q-avatar': PassThrough,
  'q-badge': PassThrough,
  'q-icon': PassThrough,
  'q-item': PassThrough,
  'q-item-section': PassThrough,
  'q-item-label': PassThrough,
  'q-list': PassThrough,
  'q-separator': PassThrough,
  'q-spinner': PassThrough,
  'q-tooltip': PassThrough,
  'q-dialog': DialogStub,
  'q-form': FormStub,
  'q-input': InputStub,
}

const perfilInicial = {
  id: 27,
  nomeCompleto: 'Ana Silva',
  email: 'ana@example.test',
  funcao: 'ADMINISTRADOR GERAL',
  status: 'ATIVO',
}

describe('Meu Perfil', () => {
  let wrapper

  beforeEach(() => {
    vi.clearAllMocks()
    mocks.get.mockResolvedValue({ data: { ...perfilInicial } })
    wrapper?.unmount()
    wrapper = null
  })

  const global = {
    stubs,
    directives: { 'close-popup': vi.fn() },
  }

  it('carrega e exibe somente os dados reais do usuario autenticado', async () => {
    wrapper = mount(PerfilPage, { global })
    await flushPromises()

    expect(mocks.get).toHaveBeenCalledWith('/auth/me')
    expect(mocks.atualizarUsuario).toHaveBeenCalledWith(perfilInicial)
    expect(wrapper.text()).toContain('Ana Silva')
    expect(wrapper.text()).toContain('ana@example.test')
    expect(wrapper.text()).toContain('Administrador Geral')
    expect(wrapper.text()).toContain('Ativa')
    expect(wrapper.text()).toContain('27')
    expect(wrapper.text()).not.toContain('Telefone')
    expect(wrapper.text()).not.toContain('Unidade Salvador')
    expect(wrapper.text()).not.toContain('Tecnologia da Informação')
  })

  it('salva nome e email, atualiza a sessao e mantem o perfil de acesso', async () => {
    wrapper = mount(PerfilPage, { global })
    await flushPromises()
    mocks.put.mockResolvedValue({
      data: {
        token: 'token-renovado',
        tipo: 'Bearer',
        usuario: {
          ...perfilInicial,
          nomeCompleto: 'Ana Souza',
          email: 'ana.souza@example.test',
        },
      },
    })

    await wrapper.findAll('button').find((button) => button.text().includes('Editar informações')).trigger('click')
    const campos = wrapper.findAll('input')
    await campos[0].setValue('Ana Souza')
    await campos[1].setValue('ana.souza@example.test')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(mocks.put).toHaveBeenCalledWith('/auth/me', {
      nomeCompleto: 'Ana Souza',
      email: 'ana.souza@example.test',
    })
    expect(mocks.atualizarToken).toHaveBeenCalledWith(
      expect.objectContaining({ token: 'token-renovado' })
    )
    expect(wrapper.text()).toContain('Ana Souza')
    expect(wrapper.text()).toContain('ana.souza@example.test')
    expect(wrapper.text()).toContain('Administrador Geral')
    expect(mocks.notify).toHaveBeenCalledWith({
      type: 'positive',
      message: 'Perfil atualizado com sucesso.',
    })
  })

  it('exibe erros da API sem descartar os dados carregados', async () => {
    wrapper = mount(PerfilPage, { global })
    await flushPromises()
    mocks.put.mockRejectedValue({
      response: { data: { mensagem: 'Este e-mail já está em uso.' } },
    })

    await wrapper.findAll('button').find((button) => button.text().includes('Editar informações')).trigger('click')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.text()).toContain('Este e-mail já está em uso.')
    expect(wrapper.text()).toContain('Ana Silva')
    expect(mocks.atualizarToken).not.toHaveBeenCalled()
  })

  it('bloqueia dados invalidos antes de chamar a API', async () => {
    wrapper = mount(PerfilPage, { global })
    await flushPromises()

    await wrapper.findAll('button').find((button) => button.text().includes('Editar informações')).trigger('click')
    await wrapper.findAll('input')[1].setValue('email-invalido')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(mocks.put).not.toHaveBeenCalled()
    expect(wrapper.text()).toContain('Informe um nome e um e-mail válidos.')
  })
})
