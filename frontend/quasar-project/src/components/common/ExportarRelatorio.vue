<template>
  <q-dialog v-model="dialogExportar">
    <q-card class="exportar-dialog">
      <q-card-section class="exportar-header">
        <div class="exportar-header-left">
          <q-avatar color="blue-1" text-color="primary" icon="download" size="50px" />
          <div class="exportar-header-info">
            <div class="exportar-title">Exportar Relatório</div>
            <div class="exportar-subtitle">Gere um relatório completo utilizando os filtros atualmente aplicados.</div>
          </div>
        </div>

        <q-btn flat round dense icon="close" class="exportar-close" @click="dialogExportar = false" />
      </q-card-section>

      <q-separator />

      <q-card-section class="exportar-content">
        <div class="titulo-secao">FORMATO DO RELATÓRIO</div>

        <q-option-group v-model="formato" :options="formatos" color="primary" />

        <div class="titulo-secao q-mt-lg">DADOS</div>
        <q-option-group v-model="modoExportacao" :options="opcoesExportacao" color="primary" />

        <div class="titulo-secao q-mt-lg">FILTROS UTILIZADOS</div>

        <q-list bordered separator class="rounded-borders">
          <q-item>
            <q-item-section avatar>
              <q-icon name="search" :color="filtroBuscaAplicado ? 'primary' : 'grey-6'" />
            </q-item-section>
            <q-item-section>
              <q-item-label>Pesquisa</q-item-label>
              <q-item-label caption>{{ texto(filtros.busca) || 'Nenhuma pesquisa aplicada' }}</q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-badge v-if="filtroBuscaAplicado" color="primary" label="Aplicado" />
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section avatar>
              <q-icon name="category" :color="filtroCategoriaAplicado ? 'primary' : 'grey-6'" />
            </q-item-section>
            <q-item-section>
              <q-item-label>Categoria</q-item-label>
              <q-item-label caption>{{ mostrarFiltro(filtros.categoria, 'Todas') }}</q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-badge v-if="filtroCategoriaAplicado" color="primary" label="Aplicado" />
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section avatar>
              <q-icon name="verified" :color="filtroStatusAplicado ? 'primary' : 'grey-6'" />
            </q-item-section>
            <q-item-section>
              <q-item-label>Status</q-item-label>
              <q-item-label caption>{{ mostrarFiltro(filtros.status, 'Todos') }}</q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-badge v-if="filtroStatusAplicado" color="primary" label="Aplicado" />
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section avatar>
              <q-icon name="apartment" :color="filtroDepartamentoAplicado ? 'primary' : 'grey-6'" />
            </q-item-section>
            <q-item-section>
              <q-item-label>Departamento</q-item-label>
              <q-item-label caption>{{ mostrarFiltro(filtros.departamento, 'Todos') }}</q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-badge v-if="filtroDepartamentoAplicado" color="primary" label="Aplicado" />
            </q-item-section>
          </q-item>
        </q-list>

        <div class="titulo-secao q-mt-lg">RESUMO</div>
        <q-banner rounded class="bg-blue-1 text-primary">
          <div class="text-subtitle1 text-weight-bold">{{ quantidadeItens }} {{ quantidadeItens === 1 ? 'item' : 'itens' }} serão exportados.</div>
          <div class="text-caption">
            <template v-if="modoExportacao === 'todos'">O relatório conterá todos os itens.</template>
            <template v-else-if="possuiFiltros">O relatório conterá somente os itens encontrados após a aplicação dos filtros selecionados.</template>
            <template v-else>Nenhum filtro foi aplicado. Todos os itens disponíveis serão considerados.</template>
          </div>
        </q-banner>
      </q-card-section>

      <q-separator />

      <q-card-actions class="exportar-footer">
        <q-btn flat color="grey-8" label="Cancelar" @click="dialogExportar = false" />
        <q-btn color="primary" icon="download" label="Exportar" :loading="exportando" :disable="quantidadeItens === 0" @click="exportarRelatorio" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'
import jsPDF from 'jspdf'
import { autoTable } from 'jspdf-autotable'
import * as XLSX from 'xlsx'

const $q = useQuasar()

const props = defineProps({
  modelValue: Boolean,
  itens: { type: Array, default: () => [] },
  filtros: { type: Object, default: () => ({ busca: '', categoria: [], status: [], departamento: [] }) }
})

const emit = defineEmits(['update:modelValue'])

const dialogExportar = computed({ get: () => props.modelValue, set: (v) => emit('update:modelValue', v) })

const exportando = ref(false)
const formato = ref('pdf')
const formatos = [ { label: 'PDF (.pdf)', value: 'pdf' }, { label: 'Excel (.xlsx)', value: 'excel' }, { label: 'CSV (.csv)', value: 'csv' } ]
const modoExportacao = ref('filtrados')
const opcoesExportacao = [ { label: 'Exportar somente os itens filtrados', value: 'filtrados' }, { label: 'Exportar todos os itens', value: 'todos' } ]

const filtros = props.filtros
// Mocks usados enquanto o backend não fornece dados via prop `itens`.
// TODO: Remover este fallback quando a API estiver disponível e sempre passar `itens` pela página.
const MOCK_ITENS = [
  { id: 'BEM-001', descricao: 'PC Dell OptiPlex', categoria: 'PC', marca: 'Dell', departamento: 'Dep. Financeiro', responsavel: 'João Silva', status: 'Ativo' },
  { id: 'BEM-002', descricao: 'Notebook Lenovo ThinkPad', categoria: 'Notebook', marca: 'Lenovo', departamento: 'Diretoria Geral', responsavel: 'Maria Souza', status: 'Manutenção' },
  { id: 'BEM-003', descricao: 'Monitor LG 24"', categoria: 'Monitor', marca: 'LG', departamento: 'Assessoria da Diretoria', responsavel: 'Carlos Andrade', status: 'Ativo' }
]

const itensDisponiveis = computed(() => (props.itens && props.itens.length) ? props.itens : MOCK_ITENS)

function texto(valor) {
  if (valor === null || valor === undefined) return ''
  if (Array.isArray(valor)) return valor.map(item => texto(item)).filter(Boolean).join(', ')
  if (typeof valor === 'object') return valor.nome || valor.label || valor.name || valor.value || ''
  return String(valor)
}

function normalizarFiltro(valor) {
  if (valor === null || valor === undefined) return []
  if (Array.isArray(valor)) return valor.map(item => texto(item).trim().toLowerCase()).filter(Boolean)
  const valorTexto = texto(valor).trim().toLowerCase()
  if (!valorTexto) return []
  return [valorTexto]
}

const filtroBuscaAplicado = computed(() => Boolean(texto(filtros?.busca).trim()))
const filtroCategoriaAplicado = computed(() => normalizarFiltro(filtros?.categoria).length > 0)
const filtroStatusAplicado = computed(() => normalizarFiltro(filtros?.status).length > 0)
const filtroDepartamentoAplicado = computed(() => normalizarFiltro(filtros?.departamento).length > 0)
const possuiFiltros = computed(() => filtroBuscaAplicado.value || filtroCategoriaAplicado.value || filtroStatusAplicado.value || filtroDepartamentoAplicado.value)

function mostrarFiltro(valor, vazio) {
  const lista = normalizarFiltro(valor)
  if (!lista.length) return vazio
  return lista.join(', ')
}

const itensFiltrados = computed(() => {
  let resultado = [...itensDisponiveis.value]
  const busca = texto(filtros?.busca).trim().toLowerCase()
  if (busca) {
    resultado = resultado.filter(item => {
      const valoresBusca = [ item.id, item.descricao, item.categoria, item.marca, item.departamento, item.responsavel, item.status ]
      return valoresBusca.some(valor => texto(valor).toLowerCase().includes(busca))
    })
  }

  const categorias = normalizarFiltro(filtros?.categoria)
  if (categorias.length) {
    resultado = resultado.filter(item => categorias.includes(texto(item.categoria || item.categoriaBem || item.tipo).trim().toLowerCase()))
  }

  const status = normalizarFiltro(filtros?.status)
  if (status.length) {
    resultado = resultado.filter(item => status.includes(texto(item.status).trim().toLowerCase()))
  }

  const departamentos = normalizarFiltro(filtros?.departamento)
  if (departamentos.length) {
    resultado = resultado.filter(item => departamentos.includes(texto(item.departamento || item.nomeDepartamento || item.idDepartamento).trim().toLowerCase()))
  }

  return resultado
})

const itensParaExportar = computed(() => modoExportacao.value === 'todos' ? itensDisponiveis.value : itensFiltrados.value)
const quantidadeItens = computed(() => itensParaExportar.value.length)

function prepararDadosRelatorio() {
  return itensParaExportar.value.map(item => ({
    ID: texto(item.id),
    Nome: texto(item.descricao),
    Categoria: texto(item.categoria || item.categoriaBem || item.tipo),
    Fabricante: texto(item.fabricante || item.marca),
    Status: texto(item.status),
    Departamento: texto(item.departamento || item.nomeDepartamento || item.idDepartamento)
  }))
}

function dataArquivo() {
  const agora = new Date()
  const ano = agora.getFullYear()
  const mes = String(agora.getMonth() + 1).padStart(2, '0')
  const dia = String(agora.getDate()).padStart(2, '0')
  return `${ano}-${mes}-${dia}`
}

function dataHoraAtual() { return new Date().toLocaleString('pt-BR') }

function baixarArquivo(blob, nomeArquivo) {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = nomeArquivo
  link.style.display = 'none'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  setTimeout(() => { window.URL.revokeObjectURL(url) }, 1000)
}

function exportarPDF() {
  const dados = prepararDadosRelatorio()
  if (!dados.length) {
    $q.notify({ type: 'warning', message: 'Não existem itens para exportar.' })
    return false
  }

  try {
    const pdf = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' })
    pdf.setFont('helvetica', 'bold')
    pdf.setFontSize(18)
    pdf.text('Relatório', 14, 18)

    pdf.setFont('helvetica', 'normal')
    pdf.setFontSize(9)
    pdf.text(`Gerado em: ${dataHoraAtual()}`, 14, 25)
    pdf.text(`Quantidade de itens: ${dados.length}`, 14, 31)

    let posicaoY = 39
    pdf.setFont('helvetica', 'bold')
    pdf.text('Filtros utilizados:', 14, posicaoY)
    posicaoY += 6

    pdf.setFont('helvetica', 'normal')
    const filtrosTexto = [
      `Pesquisa: ${texto(filtros?.busca) || 'Não aplicada'}`,
      `Categoria: ${mostrarFiltro(filtros.categoria, 'Todas')}`,
      `Status: ${mostrarFiltro(filtros.status, 'Todos')}`,
      `Departamento: ${mostrarFiltro(filtros.departamento, 'Todos')}`
    ]

    const filtrosLinha1 = [filtrosTexto[0], filtrosTexto[1]].join(' | ')
    const filtrosLinha2 = [filtrosTexto[2], filtrosTexto[3]].join(' | ')

    pdf.text(filtrosLinha1, 14, posicaoY)
    posicaoY += 5
    pdf.text(filtrosLinha2, 14, posicaoY)
    posicaoY += 8

    pdf.setFont('helvetica', 'bold')
    pdf.text('Modo de exportação:', 14, posicaoY)
    pdf.setFont('helvetica', 'normal')
    pdf.text(modoExportacao.value === 'todos' ? 'Todos os itens' : 'Somente os itens filtrados', 48, posicaoY)
    posicaoY += 8

    const colunas = Object.keys(dados[0])
    const linhas = dados.map(item => colunas.map(coluna => texto(item[coluna])))

    autoTable(pdf, {
      startY: posicaoY,
      head: [colunas],
      body: linhas,
      theme: 'grid',
      styles: { fontSize: 7, cellPadding: 2, overflow: 'linebreak' },
      headStyles: { fontSize: 7, fontStyle: 'bold' },
      alternateRowStyles: { fillColor: [245, 247, 250] },
      margin: { left: 14, right: 14 }
    })

    const numeroPaginas = pdf.internal.getNumberOfPages()
    for (let pagina = 1; pagina <= numeroPaginas; pagina++) {
      pdf.setPage(pagina)
      pdf.setFont('helvetica', 'normal')
      pdf.setFontSize(8)
      pdf.text(`Página ${pagina} de ${numeroPaginas}`, 14, pdf.internal.pageSize.height - 8)
    }

    const blob = pdf.output('blob')
    baixarArquivo(blob, `relatorio-${dataArquivo()}.pdf`)
    $q.notify({ type: 'positive', message: 'PDF exportado com sucesso.' })
    return true
  } catch (erro) {
    console.error('Erro ao gerar PDF:', erro)
    $q.notify({ type: 'negative', message: 'Não foi possível gerar o PDF.' })
    return false
  }
}

function exportarExcel() {
  const dados = prepararDadosRelatorio()
  if (!dados.length) {
    $q.notify({ type: 'warning', message: 'Não existem itens para exportar.' })
    return false
  }

  try {
    const worksheet = XLSX.utils.json_to_sheet(dados)
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Dados')
    XLSX.writeFile(workbook, `relatorio-${dataArquivo()}.xlsx`)
    $q.notify({ type: 'positive', message: 'Excel exportado com sucesso.' })
    return true
  } catch (erro) {
    console.error('Erro ao exportar Excel:', erro)
    $q.notify({ type: 'negative', message: 'Não foi possível exportar o Excel.' })
    return false
  }
}

function exportarCSV() {
  const dados = prepararDadosRelatorio()
  if (!dados.length) {
    $q.notify({ type: 'warning', message: 'Não existem itens para exportar.' })
    return false
  }

  try {
    const colunas = Object.keys(dados[0])
    const linhas = dados.map(item => colunas.map(coluna => {
      const valor = texto(item[coluna]).replace(/"/g, '""')
      return `"${valor}"`
    }).join(';'))

    const csv = [colunas.join(';'), ...linhas].join('\n')
    const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
    baixarArquivo(blob, `relatorio-${dataArquivo()}.csv`)
    $q.notify({ type: 'positive', message: 'CSV exportado com sucesso.' })
    return true
  } catch (erro) {
    console.error('Erro ao exportar CSV:', erro)
    $q.notify({ type: 'negative', message: 'Não foi possível gerar o CSV.' })
    return false
  }
}

function exportarRelatorio() {
  if (exportando.value) return
  exportando.value = true
  let sucesso = false

  try {
    switch (formato.value) {
      case 'pdf': sucesso = exportarPDF(); break
      case 'excel': sucesso = exportarExcel(); break
      case 'csv': sucesso = exportarCSV(); break
    }

    if (sucesso) dialogExportar.value = false
  } finally {
    exportando.value = false
  }
}
</script>
