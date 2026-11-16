// Runtime validation coverage for: filtered movement pagination.
import { flushPromises, mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import { afterEach, describe, expect, it } from 'vitest'
import MovimentacoesTable from './MovimentacoesTable.vue'

const registros = (quantidade) =>
  Array.from({ length: quantidade }, (_, index) => {
    const numero = index + 1
    return {
      id: numero,
      patrimonio: `PAT-${numero}`,
      codigoMovimentacao: `MOV-${String(numero).padStart(4, '0')}`,
      bem: `Bem ${numero}`,
      tipo: 'Transferencia',
      tipoClasse: 'transferencia',
      tipoIcone: 'sync_alt',
      origem: 'Origem',
      destino: 'Destino',
      data: '11/11/2026',
      responsavel: 'Responsavel',
      iniciais: 'RE',
      status: 'Concluido',
      statusClasse: 'concluido',
      statusIcone: 'check_circle_outline',
    }
  })

const stubs = {
  'q-icon': true,
  'q-chip': true,
  'q-btn': true,
  'q-card': {
    template: '<div><slot /></div>',
  },
  'q-td': true,
  'q-table': {
    name: 'QTableStub',
    props: ['rows', 'pagination'],
    template: `
      <div>
        <div
          v-for="row in rows.slice((pagination.page - 1) * pagination.rowsPerPage, pagination.page * pagination.rowsPerPage)"
          :key="row.id"
        >{{ row.codigoMovimentacao }}</div>
      </div>
    `,
  },
  'q-pagination': {
    name: 'QPagination',
    props: ['modelValue', 'max'],
    emits: ['update:modelValue'],
    template: `
      <button @click="$emit('update:modelValue', Math.min(modelValue + 1, max))">
        {{ modelValue }}
      </button>
    `,
  },
  'q-select': {
    name: 'QSelect',
    props: ['modelValue'],
    emits: ['update:modelValue'],
    template: `
      <button @click="$emit('update:modelValue', 16)">
        {{ modelValue }}
      </button>
    `,
  },
}

describe('tabela de movimentacoes', () => {
  let wrapper

  afterEach(() => wrapper?.unmount())

  it('navega entre paginas e informa o intervalo de registros exibido', async () => {
    wrapper = mount(MovimentacoesTable, {
      props: { movimentacoes: registros(17) },
      global: { stubs },
    })
    await flushPromises()

    expect(wrapper.text()).toContain('Exibindo 1–8 de 17 registros')
    expect(wrapper.text()).toContain('MOV-0001')
    expect(wrapper.text()).not.toContain('MOV-0009')

    await wrapper.findComponent({ name: 'QPagination' }).trigger('click')
    await nextTick()

    expect(wrapper.text()).toContain('Exibindo 9–16 de 17 registros')
    expect(wrapper.text()).toContain('MOV-0009')
    expect(wrapper.text()).not.toContain('MOV-0001')
  })

  it('reseta a pagina ao alterar a lista filtrada', async () => {
    wrapper = mount(MovimentacoesTable, {
      props: { movimentacoes: registros(17) },
      global: { stubs },
    })
    await flushPromises()
    await wrapper.findComponent({ name: 'QPagination' }).trigger('click')
    await wrapper.findComponent({ name: 'QPagination' }).trigger('click')
    await nextTick()

    await wrapper.setProps({ movimentacoes: registros(3) })
    await nextTick()

    expect(wrapper.text()).toContain('Exibindo 1–3 de 3 registros')
    expect(wrapper.text()).toContain('MOV-0001')
  })

  it('permite alterar a quantidade de registros por pagina', async () => {
    wrapper = mount(MovimentacoesTable, {
      props: { movimentacoes: registros(17) },
      global: { stubs },
    })
    await flushPromises()

    await wrapper.findComponent({ name: 'QSelect' }).trigger('click')
    await nextTick()

    expect(wrapper.text()).toContain('Exibindo 1–16 de 17 registros')
    expect(wrapper.text()).toContain('MOV-0016')
    expect(wrapper.text()).not.toContain('MOV-0017')
  })
})
