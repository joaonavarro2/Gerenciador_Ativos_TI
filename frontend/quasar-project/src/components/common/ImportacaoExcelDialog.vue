<template>
  <q-dialog v-model="aberto" persistent>
    <q-card class="importacao-dialog">
      <q-card-section class="row items-center justify-between">
        <div>
          <div class="text-h5 text-weight-bold">Importar {{ tipo === 'bens' ? 'bens' : 'outros itens' }} via Excel</div>
          <div class="text-caption text-grey-7">A planilha será validada antes de qualquer gravação.</div>
        </div>
        <q-btn flat round dense icon="close" :disable="processando" @click="fechar" />
      </q-card-section>

      <q-separator />

      <q-card-section class="q-gutter-md">
        <div class="row items-center q-gutter-sm">
          <q-file
            v-model="arquivo"
            outlined
            dense
            clearable
            accept=".xlsx"
            label="Planilha .xlsx"
            class="col"
            :disable="processando"
            @update:model-value="lerArquivo"
          />
          <q-btn outline color="primary" icon="download" label="Baixar modelo" no-caps @click="baixarModelo" />
        </div>

        <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative">{{ erro }}</q-banner>

        <template v-if="resultado">
          <div class="row q-col-gutter-sm">
            <div class="col-6 col-md-3"><q-card flat bordered class="q-pa-sm text-center">Identificados<strong class="block">{{ resultado.totalRegistros }}</strong></q-card></div>
            <div class="col-6 col-md-3"><q-card flat bordered class="q-pa-sm text-center">Válidos<strong class="block">{{ resultado.validos }}</strong></q-card></div>
            <div class="col-6 col-md-3"><q-card flat bordered class="q-pa-sm text-center">Importados<strong class="block">{{ resultado.importados }}</strong></q-card></div>
            <div class="col-6 col-md-3"><q-card flat bordered class="q-pa-sm text-center">Rejeitados<strong class="block">{{ resultado.rejeitados }}</strong></q-card></div>
          </div>

          <q-table
            flat
            bordered
            dense
            row-key="numeroLinha"
            :rows="resultado.linhas"
            :columns="colunasResultado"
            :pagination="{ rowsPerPage: 0 }"
            hide-bottom
            class="importacao-resultados"
          >
            <template #body-cell-erros="slotProps">
              <q-td :props="slotProps">
                <span v-if="slotProps.value.length" class="text-negative">{{ slotProps.value.join(' · ') }}</span>
                <span v-else class="text-positive">{{ slotProps.row.id ? `Importado (#${slotProps.row.id})` : 'Válido' }}</span>
              </q-td>
            </template>
          </q-table>
        </template>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat no-caps label="Fechar" :disable="processando" @click="fechar" />
        <q-btn
          v-if="linhas.length && !resultado"
          color="primary"
          no-caps
          icon="fact_check"
          label="Validar planilha"
          :loading="processando"
          @click="validarPlanilha"
        />
        <q-btn
          v-if="resultado && resultado.validos > 0 && resultado.importados === 0"
          color="positive"
          no-caps
          icon="upload"
          :label="`Confirmar importação de ${resultado.validos} registro(s)`"
          :loading="processando"
          @click="confirmarImportacao"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import * as XLSX from 'xlsx'
import { api } from '@/boot/axios'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  tipo: { type: String, required: true, validator: value => ['bens', 'outros-itens'].includes(value) },
  opcoes: { type: Object, default: () => ({}) },
})

const emit = defineEmits(['update:modelValue', 'concluido'])
const $q = useQuasar()
const aberto = computed({ get: () => props.modelValue, set: value => emit('update:modelValue', value) })
const arquivo = ref(null)
const linhas = ref([])
const resultado = ref(null)
const erro = ref('')
const processando = ref(false)

const colunas = {
  bens: [
    { key: 'codigo', label: 'Código' },
    { key: 'patrimonio', label: 'Patrimônio' },
    { key: 'instituicao', label: 'Instituição' },
    { key: 'nome', label: 'Nome' },
    { key: 'categoria', label: 'Categoria' },
    { key: 'serial', label: 'Número de Série' },
    { key: 'fabricante', label: 'Fabricante' },
    { key: 'modelo', label: 'Modelo' },
    { key: 'descricao', label: 'Descrição' },
    { key: 'status', label: 'Status' },
    { key: 'dataAquisicao', label: 'Data de Aquisição' },
    { key: 'escritorio', label: 'Escritório' },
    { key: 'departamento', label: 'Departamento' },
    { key: 'responsavel', label: 'Responsável' },
  ],
  'outros-itens': [
    { key: 'codigo', label: 'Código' },
    { key: 'patrimonio', label: 'Patrimônio' },
    { key: 'instituicao', label: 'Instituição' },
    { key: 'nome', label: 'Nome' },
    { key: 'categoria', label: 'Categoria' },
    { key: 'escritorio', label: 'Escritório' },
    { key: 'localizacao', label: 'Localização' },
    { key: 'quantidade', label: 'Quantidade' },
    { key: 'estoqueMinimo', label: 'Estoque Mínimo' },
    { key: 'fornecedor', label: 'Fornecedor' },
    { key: 'status', label: 'Status' },
  ],
}

const camposImportados = computed(() => colunas[props.tipo])
const colunasResultado = [
  { name: 'numeroLinha', label: 'LINHA', field: 'numeroLinha', align: 'left' },
  { name: 'erros', label: 'VALIDAÇÃO / RESULTADO', field: 'erros', align: 'left' },
]

watch(() => props.modelValue, visivel => {
  if (!visivel) limpar()
})

async function lerArquivo(novoArquivo) {
  limpar()
  if (!novoArquivo) return
  if (!/\.xlsx$/i.test(novoArquivo.name)) {
    erro.value = 'Selecione um arquivo .xlsx.'
    return
  }

  try {
    const workbook = XLSX.read(await novoArquivo.arrayBuffer(), { type: 'array', cellDates: false })
    const primeiraPlanilha = workbook.Sheets[workbook.SheetNames[0]]
    if (!primeiraPlanilha) throw new Error('A planilha não contém uma aba para leitura.')

    const matriz = XLSX.utils.sheet_to_json(primeiraPlanilha, {
      header: 1,
      raw: false,
      defval: '',
      blankrows: true,
    })
    if (matriz.length < 2) throw new Error('A planilha está vazia ou contém somente o cabeçalho.')

    const cabecalhos = matriz[0].map(normalizarCabecalho)
    const mapaColunas = new Map()
    camposImportados.value.forEach(campo => {
      const posicao = cabecalhos.indexOf(normalizarCabecalho(campo.label))
      if (posicao >= 0) mapaColunas.set(campo.key, posicao)
    })

    const obrigatorios = props.tipo === 'bens'
      ? ['codigo', 'instituicao', 'nome', 'categoria', 'fabricante', 'modelo', 'status', 'dataAquisicao', 'escritorio', 'departamento']
      : ['codigo', 'instituicao', 'nome', 'categoria', 'escritorio', 'localizacao', 'quantidade', 'estoqueMinimo', 'status']
    const ausentes = obrigatorios.filter(campo => !mapaColunas.has(campo))
    if (ausentes.length) {
      throw new Error(`Colunas obrigatórias ausentes: ${ausentes.map(campo => camposImportados.value.find(item => item.key === campo).label).join(', ')}.`)
    }

    linhas.value = matriz.slice(1).flatMap((colunasLinha, indice) => {
      const dados = Object.fromEntries([...mapaColunas].map(([campo, posicao]) => [
        campo,
        String(colunasLinha[posicao] ?? '').trim(),
      ]))
      if (Object.values(dados).every(valor => valor === '')) return []
      return [{ numeroLinha: indice + 2, dados }]
    })
    if (!linhas.value.length) throw new Error('A planilha não contém registros para importar.')
  } catch (errorLeitura) {
    erro.value = errorLeitura.message || 'Não foi possível ler a planilha.'
  }
}

async function validarPlanilha() {
  processando.value = true
  erro.value = ''
  try {
    const { data } = await api.post(`/importacao-excel/${props.tipo}/validar`, { linhas: linhas.value })
    resultado.value = data
  } catch (errorValidacao) {
    erro.value = mensagemErro(errorValidacao, 'validar a planilha')
  } finally {
    processando.value = false
  }
}

async function confirmarImportacao() {
  processando.value = true
  erro.value = ''
  try {
    const { data } = await api.post(`/importacao-excel/${props.tipo}/confirmar`, { linhas: linhas.value })
    resultado.value = data
    emit('concluido', data)
    $q.notify({ type: 'positive', message: `${data.importados} registro(s) importado(s); ${data.rejeitados} rejeitado(s).` })
  } catch (errorImportacao) {
    erro.value = mensagemErro(errorImportacao, 'importar os registros')
  } finally {
    processando.value = false
  }
}

function baixarModelo() {
  const cabecalho = camposImportados.value.map(campo => campo.label)
  const worksheet = XLSX.utils.aoa_to_sheet([cabecalho])
  worksheet['!cols'] = cabecalho.map(() => ({ wch: 22 }))
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, 'Importação')
  XLSX.writeFile(workbook, `modelo-importacao-${props.tipo}.xlsx`)
}

function normalizarCabecalho(valor) {
  return String(valor ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[^a-zA-Z0-9]/g, '').toLowerCase()
}

function mensagemErro(error, acao) {
  const mensagem = error.response?.data?.message || error.response?.data?.mensagem || error.response?.data?.detail
  if (mensagem) return mensagem
  if (error.response?.status === 401) return 'Sua sessão expirou. Entre novamente para continuar.'
  if (error.response?.status === 403) return 'Sua conta não tem permissão para importar registros.'
  if (error.response) return `Não foi possível ${acao} (HTTP ${error.response.status}).`
  return `Não foi possível conectar ao servidor para ${acao}.`
}

function limpar() {
  arquivo.value = null
  linhas.value = []
  resultado.value = null
  erro.value = ''
}

function fechar() {
  if (processando.value) return
  aberto.value = false
}
</script>

<style scoped>
.importacao-dialog { width: min(1000px, 94vw); max-width: 94vw; max-height: 90vh; display: flex; flex-direction: column; }
.importacao-resultados { max-height: 45vh; overflow: auto; }
</style>
