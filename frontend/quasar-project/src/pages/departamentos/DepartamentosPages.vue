<template>
  <q-page class="departamentos-page">
    <DepartamentosHeader
      v-model:filtros="filtros"
      :total-departamentos="departamentos.length"
      :total-bens="totalBens"
      @novo-departamento="abrirNovoDepartamento"
    />

    <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative q-mt-md">
      {{ erro }}
      <template #action><q-btn flat color="negative" label="Tentar novamente" @click="carregarDados" /></template>
    </q-banner>
    <q-inner-loading :showing="carregandoLista"><q-spinner size="40px" color="primary" /></q-inner-loading>

    <NovoDepartamento
      v-model="mostrarNovoDepartamento"
      :escritorios="escritorios"
      :salvando="salvando"
      :erro="erroFormulario"
      @salvar="salvarNovoDepartamento"
    />

    <q-dialog v-model="mostrarConfirmacaoExclusao" persistent>
      <q-card style="width: 480px; max-width: 92vw; border-radius: 16px">
        <q-card-section class="row items-center no-wrap">
          <q-avatar color="red-1" text-color="negative" icon="delete_outline" size="44px" />
          <div class="q-ml-md">
            <div class="text-h6 text-weight-bold">Excluir departamento</div>
            <div class="text-body2 text-grey-7">Confirme a exclusão de {{ departamentoParaExcluir?.nome }}.</div>
          </div>
          <q-space />
          <q-btn flat round dense icon="close" :disable="excluindo" @click="fecharConfirmacaoExclusao" />
        </q-card-section>
        <q-card-section class="q-pt-none">
          <q-banner v-if="erroExclusao" dense rounded class="bg-red-1 text-negative">{{ erroExclusao }}</q-banner>
          <div v-else class="text-body2">Esta ação não pode ser desfeita.</div>
        </q-card-section>
        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" :disable="excluindo" @click="fecharConfirmacaoExclusao" />
          <q-btn color="negative" icon="delete" label="Excluir" :loading="excluindo" @click="excluirDepartamento" />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <DepartamentosTable
      v-if="departamentoSelecionado === null"
      :departamentos="departamentosFiltrados"
      @visualizar-departamento="abrirVisualizacaoDepartamento"
      @solicitar-exclusao="confirmarExclusaoDepartamento"
    />
    <VisualizacaoDepartamento
      v-else
      :departamento="departamentoSelecionado"
      @voltar="voltarDepartamentos"
    />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '@/boot/axios.js'
import DepartamentosHeader from '@/components/departamentos/DepartamentosHeader.vue'
import DepartamentosTable from '@/components/departamentos/DepartamentosTable.vue'
import VisualizacaoDepartamento from '@/components/departamentos/cards/VisualizacaoDepartamento.vue'
import NovoDepartamento from '@/components/departamentos/cards/NovoDepartamento.vue'

const departamentos = ref([])
const escritorios = ref([])
const filtros = ref({ busca: '', departamento: null, cidade: null, status: null })
const departamentoSelecionado = ref(null)
const mostrarNovoDepartamento = ref(false)
const carregandoLista = ref(false)
const salvando = ref(false)
const erro = ref('')
const erroFormulario = ref('')
const mostrarConfirmacaoExclusao = ref(false)
const departamentoParaExcluir = ref(null)
const erroExclusao = ref('')
const excluindo = ref(false)
const totalBens = computed(() => departamentos.value.reduce((total, departamento) => total + (departamento.bens || 0), 0))

function corDepartamento(id) {
  const cores = [['#2563EB', '#DBEAFE'], ['#DB2777', '#FCE7F3'], ['#059669', '#D1FAE5'], ['#D97706', '#FEF3C7'], ['#7C3AED', '#EDE9FE'], ['#DC2626', '#FEE2E2']]
  return cores[(Number(id) || 0) % cores.length]
}

function mapearDepartamento(item) {
  const [cor, corFundo] = corDepartamento(item.id)
  return {
    ...item,
    sigla: item.sigla || String(item.nome || '').trim().split(/\s+/).slice(0, 2).map((parte) => parte[0]).join('').toUpperCase(),
    bens: Number(item.bens || 0),
    consertos: Number(item.consertos || 0),
    descricao: item.descricao || '',
    cor,
    corFundo,
  }
}

async function carregarDados() {
  carregandoLista.value = true
  erro.value = ''
  try {
    const [resDepartamentos, resEscritorios] = await Promise.all([
      api.get('/departamentos'),
      api.get('/escritorios'),
    ])
    departamentos.value = resDepartamentos.data.map(mapearDepartamento)
    escritorios.value = resEscritorios.data.map((item) => ({ id: item.id, label: item.nome, value: item.id }))
  } catch (error) {
    erro.value = mensagemErro(error, 'carregar os departamentos')
  } finally { carregandoLista.value = false }
}

function mensagemErro(error, acao) {
  if (error.response?.data?.mensagem) return error.response.data.mensagem
  if (error.response?.status === 401) return 'Sua sessão expirou. Entre novamente para continuar.'
  if (error.response?.status === 403) return 'Sua conta não tem permissão para acessar os departamentos.'
  if (error.response) return `Não foi possível ${acao} (HTTP ${error.response.status}).`
  return `Não foi possível conectar ao servidor para ${acao}. Verifique se o backend está ativo.`
}

function abrirNovoDepartamento() {
  erroFormulario.value = ''
  mostrarNovoDepartamento.value = true
}

async function salvarNovoDepartamento(dados) {
  if (salvando.value) return
  salvando.value = true
  erroFormulario.value = ''
  try {
    await api.post('/departamentos', {
      nome: dados.nome?.trim(),
      escritorioId: dados.escritorioId || null,
      status: dados.status || 'Ativo',
    })
    mostrarNovoDepartamento.value = false
    await carregarDados()
  } catch (error) {
    erroFormulario.value = mensagemErro(error, 'cadastrar o departamento')
  } finally { salvando.value = false }
}

function abrirVisualizacaoDepartamento(departamento) {
  departamentoSelecionado.value = departamento
}

function confirmarExclusaoDepartamento(departamento) {
  departamentoParaExcluir.value = departamento
  erroExclusao.value = ''
  mostrarConfirmacaoExclusao.value = true
}

function fecharConfirmacaoExclusao() {
  if (excluindo.value) return
  mostrarConfirmacaoExclusao.value = false
  departamentoParaExcluir.value = null
  erroExclusao.value = ''
}

async function excluirDepartamento() {
  const id = departamentoParaExcluir.value?.id
  if (!id || excluindo.value) return
  excluindo.value = true
  erroExclusao.value = ''
  try {
    await api.delete(`/departamentos/${id}`)
    mostrarConfirmacaoExclusao.value = false
    departamentoParaExcluir.value = null
    await carregarDados()
  } catch (error) {
    erroExclusao.value = mensagemErro(error, 'excluir o departamento')
  } finally { excluindo.value = false }
}

function voltarDepartamentos() {
  departamentoSelecionado.value = null
  carregarDados()
}

const normalizar = (valor) => String(valor ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').trim().toLocaleLowerCase('pt-BR')
const departamentosFiltrados = computed(() => departamentos.value.filter((departamento) => {
  const busca = normalizar(filtros.value.busca)
  const texto = normalizar(`${departamento.nome} ${departamento.sigla} ${departamento.escritorio || ''}`)
  return (!busca || texto.includes(busca)) && (!filtros.value.status || normalizar(departamento.status) === normalizar(filtros.value.status))
}))

onMounted(carregarDados)
</script>
