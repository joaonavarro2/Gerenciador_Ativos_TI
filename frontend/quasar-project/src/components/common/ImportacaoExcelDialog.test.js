import { mount, flushPromises } from '@vue/test-utils'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import ImportacaoExcelDialog from './ImportacaoExcelDialog.vue'
import { api } from '@/boot/axios'
import * as XLSX from 'xlsx'

vi.mock('@/boot/axios', () => ({ api: { post: vi.fn() } }))
vi.mock('quasar', () => ({
  useQuasar: () => ({ notify: vi.fn() }),
}))
vi.mock('xlsx', () => ({
  read: vi.fn(),
  utils: {
    sheet_to_json: vi.fn(),
    aoa_to_sheet: vi.fn(() => ({})),
    book_new: vi.fn(() => ({})),
    book_append_sheet: vi.fn(),
    writeFile: vi.fn(),
  },
}))

const QDialogStub = {
  props: ['modelValue'],
  template: '<div v-if="modelValue"><slot /></div>',
}
const QBtnStub = {
  props: ['label', 'loading', 'disable'],
  emits: ['click'],
  template: '<button :disabled="disable || loading" @click="$emit(\'click\')">{{ label }}<slot /></button>',
}
const QFileStub = {
  emits: ['update:modelValue'],
  template: '<input aria-label="Planilha .xlsx" type="file" @change="$emit(\'update:modelValue\', $event.target.files[0])" />',
}
const QBannerStub = { template: '<div role="alert"><slot /></div>' }
const SlotStub = { template: '<div><slot /></div>' }
const QTableStub = {
  props: ['rows'],
  template: '<div data-test="result-count"><span v-for="row in rows" :key="row.numeroLinha">{{ row.id ? `Importado (#${row.id})` : "Válido" }}</span></div>',
}

function planilhaMock() {
  XLSX.read.mockReturnValue({ SheetNames: ['Importação'], Sheets: { Importação: {} } })
  XLSX.utils.sheet_to_json.mockReturnValue([
    ['Código', 'Patrimônio', 'Instituição', 'Nome', 'Categoria', 'Número de Série', 'Fabricante', 'Modelo', 'Descrição', 'Status', 'Data de Aquisição', 'Escritório', 'Departamento', 'Responsável'],
    ['AUTO-001', '', 'CAR', 'Notebook São João', 'Notebook', '', 'Marca', 'Modelo', 'Ação çã', 'Ativo', '2024-02-29', 'Salvador', 'Tecnologia', ''],
  ])
}

function arquivo(nome = 'itens.xlsx') {
  return { name: nome, arrayBuffer: vi.fn().mockResolvedValue(new ArrayBuffer(8)) }
}

function montar() {
  return mount(ImportacaoExcelDialog, {
    props: { modelValue: true, tipo: 'bens' },
    global: {
      stubs: {
        QDialog: QDialogStub,
        QCard: SlotStub,
        QCardSection: SlotStub,
        QCardActions: SlotStub,
        QSeparator: SlotStub,
        QBtn: QBtnStub,
        QFile: QFileStub,
        QBanner: QBannerStub,
        QTable: QTableStub,
        QTd: true,
      },
    },
  })
}

async function clicar(wrapper, texto) {
  const botao = wrapper.findAll('button').find(item => item.text().includes(texto))
  if (!botao) throw new Error(`Botão não encontrado: ${texto}`)
  await botao.trigger('click')
}

async function selecionarArquivo(wrapper, novoArquivo) {
  const input = wrapper.get('input[type="file"]')
  Object.defineProperty(input.element, 'files', { configurable: true, value: [novoArquivo] })
  await input.trigger('change')
  await flushPromises()
}

describe('ImportacaoExcelDialog', () => {
  beforeEach(() => { vi.clearAllMocks() })
  afterEach(() => { vi.restoreAllMocks() })

  it('lê XLSX, valida e confirma via API exibindo o resultado', async () => {
    planilhaMock()
    api.post.mockResolvedValueOnce({ data: {
      totalRegistros: 1, validos: 1, importados: 0, rejeitados: 0,
      linhas: [{ numeroLinha: 2, id: null, dados: {}, erros: [] }],
    } }).mockResolvedValueOnce({ data: {
      totalRegistros: 1, validos: 1, importados: 1, rejeitados: 0,
      linhas: [{ numeroLinha: 2, id: 901, dados: {}, erros: [] }],
    } })
    const wrapper = montar()

    await selecionarArquivo(wrapper, arquivo())
    expect(XLSX.read).toHaveBeenCalledOnce()
    expect(XLSX.utils.sheet_to_json).toHaveBeenCalledOnce()
    await clicar(wrapper, 'Validar planilha')
    await flushPromises()
    expect(api.post).toHaveBeenNthCalledWith(1, '/importacao-excel/bens/validar', expect.objectContaining({ linhas: [expect.objectContaining({ numeroLinha: 2 })] }))
    expect(wrapper.text()).toContain('Válidos')
    expect(wrapper.text()).toContain('Confirmar importação de 1 registro(s)')

    await clicar(wrapper, 'Confirmar importação')
    await flushPromises()
    expect(api.post).toHaveBeenNthCalledWith(2, '/importacao-excel/bens/confirmar', expect.any(Object))
    expect(wrapper.emitted('concluido')).toHaveLength(1)
    expect(wrapper.text()).toContain('Importado (#901)')
  })

  it('rejeita extensão não aceita sem enviar à API', async () => {
    const wrapper = montar()
    await selecionarArquivo(wrapper, arquivo('itens.pdf'))
    expect(wrapper.get('[role="alert"]').text()).toContain('Selecione um arquivo .xlsx')
    expect(api.post).not.toHaveBeenCalled()
  })

  it('rejeita planilha sem registros e cabeçalho obrigatório ausente', async () => {
    XLSX.read.mockReturnValue({ SheetNames: ['Dados'], Sheets: { Dados: {} } })
    XLSX.utils.sheet_to_json.mockReturnValue([['Código'], ['']])
    const wrapper = montar()
    await selecionarArquivo(wrapper, arquivo())
    expect(wrapper.get('[role="alert"]').text()).toContain('Colunas obrigatórias ausentes')
    expect(api.post).not.toHaveBeenCalled()

    XLSX.utils.sheet_to_json.mockReturnValue([['Código'], ['valor']])
    await selecionarArquivo(wrapper, arquivo())
    expect(wrapper.get('[role="alert"]').text()).toContain('Colunas obrigatórias ausentes')
  })

  it('mostra erro para XLSX corrompido e não chama a API', async () => {
    XLSX.read.mockImplementation(() => { throw new Error('Arquivo corrompido') })
    const wrapper = montar()
    await selecionarArquivo(wrapper, arquivo())
    expect(wrapper.get('[role="alert"]').text()).toContain('Arquivo corrompido')
    expect(api.post).not.toHaveBeenCalled()
  })

  it('bloqueia o botão durante validação pendente e permite fechar sem importar', async () => {
    planilhaMock()
    let concluir
    api.post.mockReturnValue(new Promise(resolve => { concluir = resolve }))
    const wrapper = montar()
    await selecionarArquivo(wrapper, arquivo())
    await clicar(wrapper, 'Validar planilha')
    expect(api.post).toHaveBeenCalledOnce()
    expect(wrapper.findAll('button').some(button => button.text().includes('Validar planilha') && button.element.disabled)).toBe(true)
    concluir({ data: { totalRegistros: 1, validos: 1, importados: 0, rejeitados: 0, linhas: [] } })
    await flushPromises()
    expect(wrapper.findAll('button').some(button => button.text().includes('Confirmar importação'))).toBe(true)
    await clicar(wrapper, 'Fechar')
    expect(wrapper.emitted('update:modelValue')).toContainEqual([false])
    expect(api.post).toHaveBeenCalledOnce()
  })

  it('apresenta falha da API e habilita novamente as ações', async () => {
    planilhaMock()
    api.post.mockRejectedValueOnce({ response: { status: 409, data: { mensagem: 'Código duplicado' } } })
    const wrapper = montar()
    await selecionarArquivo(wrapper, arquivo())
    await clicar(wrapper, 'Validar planilha')
    await flushPromises()
    expect(wrapper.get('[role="alert"]').text()).toContain('Código duplicado')
    expect(wrapper.findAll('button').some(button => button.element.disabled)).toBe(false)
  })
})
