<template>
  <q-page class="consertos-page">
    <ConsertosHeader v-model:filtros="filtros" @novo="abrirModalNovoReparo" />
    <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative q-mb-md">{{ erro }}</q-banner>
    <q-card flat bordered class="q-mb-md">
      <q-inner-loading :showing="carregando"><q-spinner size="40px" color="primary" /></q-inner-loading>
      <ConsertosTable :consertos="consertosFiltrados" :possui-filtros="possuiFiltros" :filtros="filtros"
        @visualizar="abrirModalVisualizacao" @editar="abrirModalEdicao" @excluir="abrirModalExclusao" />
    </q-card>
    <NovoReparo v-model="dialogNovoReparo" :salvando="salvando" @salvar="adicionarReparo" />
    <EditarReparo v-model="dialogEditarReparo" :reparo="reparoSelecionado" :salvando="salvando" @salvar="atualizarReparo" />
    <VisualizarReparo v-model="dialogVisualizarReparo" :reparo="reparoSelecionado" />
    <ExcluirReparo v-model="dialogExcluirReparo" :reparo="reparoSelecionado" @excluir="excluirReparo" />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Notify } from 'quasar'
import { api } from '@/boot/axios.js'
import ConsertosHeader from '@/components/consertos/ConsertosHeader.vue'
import ConsertosTable from '@/components/consertos/ConsertosTable.vue'
import NovoReparo from '@/components/consertos/card/NovoReparo.vue'
import EditarReparo from '@/components/consertos/card/EditarReparo.vue'
import VisualizarReparo from '@/components/consertos/card/VisualizarReparo.vue'
import ExcluirReparo from '@/components/consertos/card/ExcluirReparo.vue'

const dialogNovoReparo = ref(false)
const dialogEditarReparo = ref(false)
const dialogVisualizarReparo = ref(false)
const dialogExcluirReparo = ref(false)
const reparoSelecionado = ref({})
const consertos = ref([])
const carregando = ref(false)
const salvando = ref(false)
const erro = ref('')
const filtros = ref({ busca: '', tipo: null, status: null, tecnico: null, departamento: null, escritorio: null, dataInicial: '', dataFinal: '' })
const possuiFiltros = computed(() => Object.values(filtros.value).some(Boolean))
const texto = (valor) => String(valor ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').trim().toLocaleLowerCase('pt-BR')
const formatarData = (valor) => valor ? valor.slice(0, 10) : ''

function mapear(reparo) {
  return {
    ...reparo,
    ativoId: reparo.codigoBem || reparo.patrimonioBem || `Bem #${reparo.itemId}`,
    problema: reparo.descricaoProblema,
    descricao: reparo.descricaoSolucao || '',
    data: formatarData(reparo.dataEntrada),
    dataInicio: formatarData(reparo.dataEntrada),
    dataConclusao: formatarData(reparo.dataSaida),
  }
}

async function carregarReparos() {
  carregando.value = true
  erro.value = ''
  try {
    const { data } = await api.get('/consertos')
    consertos.value = data.map(mapear)
  } catch (e) {
    erro.value = e.response?.data?.mensagem || 'Não foi possível carregar os reparos.'
  } finally { carregando.value = false }
}

function abrirModalNovoReparo() { dialogNovoReparo.value = true }
function abrirModalEdicao(reparo) { reparoSelecionado.value = reparo; dialogEditarReparo.value = true }
function abrirModalVisualizacao(reparo) { reparoSelecionado.value = reparo; dialogVisualizarReparo.value = true }
function abrirModalExclusao(reparo) { reparoSelecionado.value = reparo; dialogExcluirReparo.value = true }

function corpo(reparo) {
  return {
    tipo: reparo.tipo,
    descricaoProblema: reparo.descricaoProblema || reparo.problema,
    descricaoSolucao: reparo.descricaoSolucao || reparo.descricao || null,
    dataEntrada: reparo.dataEntrada || `${reparo.dataInicio || reparo.data}T00:00:00`,
    dataSaida: reparo.dataSaida || (reparo.dataConclusao ? `${reparo.dataConclusao}T00:00:00` : null),
    status: reparo.status,
    tecnico: reparo.tecnico,
    itemId: reparo.itemId,
  }
}

function mensagemErroApi(erro, acao) {
  const dados = erro.response?.data
  if (typeof dados?.mensagem === 'string' && dados.mensagem.trim()) return dados.mensagem

  // A API também pode responder apenas com um mapa de campos inválidos.
  if (dados && typeof dados === 'object') {
    const detalhes = Object.entries(dados)
      .filter(([campo, valor]) => campo !== 'timestamp' && typeof valor === 'string')
      .map(([campo, valor]) => `${campo}: ${valor}`)
    if (detalhes.length) return detalhes.join(' · ')
  }

  if (erro.response) return `${acao} (HTTP ${erro.response.status}). Confira os dados e tente novamente.`
  return `Não foi possível conectar ao servidor para ${acao.toLocaleLowerCase('pt-BR')}. Verifique se o backend está ativo.`
}

async function enviarAnexos(id, arquivos) {
  if (!arquivos?.length) return
  const formData = new FormData()
  arquivos.forEach((arquivo) => formData.append('arquivos', arquivo))
  await api.post(`/consertos/${id}/anexos`, formData)
}

async function adicionarReparo(reparo) {
  salvando.value = true
  try {
    const { data } = await api.post('/consertos', corpo(reparo))
    dialogNovoReparo.value = false
    await carregarReparos()
    try {
      await enviarAnexos(data.id, reparo.anexos)
      Notify.create({ type: 'positive', message: 'Reparo registrado.' })
    } catch (e) {
      Notify.create({ type: 'warning', message: e.response?.data?.mensagem || 'Reparo registrado, mas não foi possível enviar os anexos. Reenvie pela edição.' })
    }
  } catch (e) {
    console.error('Falha ao registrar reparo', {
      status: e.response?.status,
      resposta: e.response?.data,
      url: e.config?.baseURL ? `${e.config.baseURL}${e.config.url || ''}` : e.config?.url,
      payload: corpo(reparo),
      erro: e.message,
    })
    Notify.create({ type: 'negative', message: mensagemErroApi(e, 'Registrar reparo'), timeout: 8000 })
    await carregarReparos()
  } finally { salvando.value = false }
}

async function atualizarReparo(reparo) {
  salvando.value = true
  try {
    await api.put(`/consertos/${reparo.id}`, corpo(reparo))
    dialogEditarReparo.value = false
    await carregarReparos()
    try {
      await enviarAnexos(reparo.id, reparo.anexos)
      Notify.create({ type: 'positive', message: 'Reparo atualizado.' })
    } catch (e) {
      Notify.create({ type: 'warning', message: e.response?.data?.mensagem || 'Reparo atualizado, mas não foi possível enviar os anexos.' })
    }
  } catch (e) {
    console.error('Falha ao atualizar reparo', {
      status: e.response?.status,
      resposta: e.response?.data,
      erro: e.message,
    })
    Notify.create({ type: 'negative', message: mensagemErroApi(e, 'Atualizar reparo'), timeout: 8000 })
    await carregarReparos()
  } finally { salvando.value = false }
}

async function excluirReparo(id) {
  try {
    await api.delete(`/consertos/${id}`)
    await carregarReparos()
    Notify.create({ type: 'positive', message: 'Reparo excluído.' })
  } catch (e) {
    Notify.create({ type: 'negative', message: e.response?.data?.mensagem || 'Não foi possível excluir o reparo.' })
  }
}

const consertosFiltrados = computed(() => consertos.value.filter((item) => {
  const busca = texto(filtros.value.busca)
  const contemBusca = !busca || texto(`${item.ativoId} ${item.problema} ${item.descricao} ${item.tecnico}`).includes(busca)
  const matches = (campo, filtro) => !filtro || texto(campo) === texto(filtro)
  const desde = !filtros.value.dataInicial || item.data >= filtros.value.dataInicial
  const ate = !filtros.value.dataFinal || item.data <= filtros.value.dataFinal
  return contemBusca && matches(item.status, filtros.value.status) && matches(item.tipo, filtros.value.tipo)
    && matches(item.tecnico, filtros.value.tecnico) && matches(item.departamento, filtros.value.departamento)
    && matches(item.escritorio, filtros.value.escritorio) && desde && ate
}))

onMounted(carregarReparos)
</script>

<style scoped>
.consertos-page { padding: 16px; }
</style>
