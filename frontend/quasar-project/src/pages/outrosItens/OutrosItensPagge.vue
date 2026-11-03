<template>
  <q-page class="outros-itens-page">
    <HeaderOutros v-model:filtros="filtros" :opcoes-escritorios="nomesEscritorios" :opcoes-categorias="categorias" @novo-item="abrirNovoItem" />

    <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative q-mt-md">
      {{ erro }}
      <template #action><q-btn flat color="negative" label="Tentar novamente" @click="carregarItens" /></template>
    </q-banner>

    <q-card flat bordered class="q-mt-md outros-itens-lista">
      <q-inner-loading :showing="carregando"><q-spinner size="40px" color="primary" /></q-inner-loading>
      <TableOutros :itens="itensFiltrados" :possui-filtros="possuiFiltros" @visualizar="visualizarItem" @editar="editarItem" @excluir="excluirItem" />
    </q-card>

    <NovoItem v-model="dialogNovoItem" :escritorios="nomesEscritorios" :categorias="categorias" @salvar="adicionarItem" />
    <VisualizarItem v-model="dialogVisualizarItem" :item="itemSelecionado" />
    <EditarItem v-model="dialogEditarItem" :item="itemSelecionado" :escritorios="nomesEscritorios" :categorias="categorias" @salvar="atualizarItem" />
    <ExcluirItem v-model="dialogExcluirItem" :item="itemSelecionado" @excluir="removerItem" />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import { api } from '@/boot/axios.js'
import HeaderOutros from '@/components/outrosItens/HeaderOutros.vue'
import TableOutros from '@/components/outrosItens/TableOutros.vue'
import NovoItem from '@/components/outrosItens/card/NovoItem.vue'
import VisualizarItem from '@/components/outrosItens/card/VisualizarItem.vue'
import EditarItem from '@/components/outrosItens/card/EditarItem.vue'
import ExcluirItem from '@/components/outrosItens/card/ExcluirItem.vue'

const $q = useQuasar()
const itens = ref([])
const escritorios = ref([])
const carregando = ref(false)
const erro = ref('')
const dialogNovoItem = ref(false)
const dialogVisualizarItem = ref(false)
const dialogEditarItem = ref(false)
const dialogExcluirItem = ref(false)
const itemSelecionado = ref({})
const filtros = ref({ busca: '', categoria: null, escritorio: null, status: null })
const nomesEscritorios = computed(() => escritorios.value.map(item => item.nome))
const categorias = computed(() => [...new Set([...itens.value.map(item => item.categoria), 'Material de escrit\u00f3rio', 'Suprimentos', 'Mobili\u00e1rio', 'Outros'].filter(Boolean))].sort())
const possuiFiltros = computed(() => Object.values(filtros.value).some(value => value !== null && value !== ''))
const normalizar = valor => String(valor ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').trim().toLocaleLowerCase('pt-BR')
const itensFiltrados = computed(() => {
  const filtro = filtros.value
  const busca = normalizar(filtro.busca)
  return itens.value.filter(item => {
    const texto = normalizar(`${item.codigo} ${item.nome} ${item.categoria} ${item.escritorio} ${item.localizacao} ${item.fornecedor}`)
    return (!busca || texto.includes(busca))
      && (!filtro.categoria || normalizar(item.categoria) === normalizar(filtro.categoria))
      && (!filtro.escritorio || item.escritorio === filtro.escritorio)
      && (!filtro.status || item.status === filtro.status)
  })
})

async function carregarItens() {
  carregando.value = true
  erro.value = ''
  try {
    const [resItens, resEscritorios] = await Promise.all([api.get('/outros-itens'), api.get('/escritorios')])
    escritorios.value = resEscritorios.data
    itens.value = resItens.data.map(mapearItem)
  } catch (error) {
    erro.value = mensagemErro(error, 'carregar os itens de estoque')
  } finally { carregando.value = false }
}

function mapearItem(item) {
  return { ...item, quantidade: Number(item.quantidade), estoqueMinimo: Number(item.estoqueMinimo), fornecedor: item.fornecedor || '' }
}

function payload(item) {
  const escritorio = escritorios.value.find(opcao => opcao.nome === item.escritorio)
  return {
    codigo: item.codigo?.trim(), nome: item.nome?.trim(), categoria: item.categoria,
    escritorioId: item.escritorioId || escritorio?.id,
    localizacao: item.localizacao?.trim(), quantidade: Number(item.quantidade),
    estoqueMinimo: Number(item.estoqueMinimo), fornecedor: item.fornecedor?.trim() || null,
    status: item.status,
  }
}

function mensagemErro(error, acao) {
  const mensagem = error.response?.data?.mensagem || error.response?.data?.message || error.response?.data?.detail
  if (mensagem) return mensagem
  if (error.response?.status === 401) return 'Sua sessão expirou. Entre novamente para continuar.'
  if (error.response?.status === 403) return 'Sua conta não tem permissão para acessar os itens de estoque.'
  if (error.response) return `Não foi possível ${acao} (HTTP ${error.response.status}).`
  return `Não foi possível conectar ao servidor para ${acao}. Verifique se o backend está ativo.`
}

function notificarErro(error, acao) { $q.notify({ type: 'negative', message: mensagemErro(error, acao), timeout: 5000 }) }
function abrirNovoItem() { dialogNovoItem.value = true }
function visualizarItem(item) { itemSelecionado.value = item; dialogVisualizarItem.value = true }
function editarItem(item) { itemSelecionado.value = item; dialogEditarItem.value = true }
function excluirItem(item) { itemSelecionado.value = item; dialogExcluirItem.value = true }

async function adicionarItem(item) {
  try { await api.post('/outros-itens', payload(item)); dialogNovoItem.value = false; await carregarItens() }
  catch (error) { notificarErro(error, 'cadastrar o item') }
}

async function atualizarItem(item) {
  try { await api.put(`/outros-itens/${item.id}`, payload(item)); dialogEditarItem.value = false; await carregarItens() }
  catch (error) { notificarErro(error, 'atualizar o item') }
}

async function removerItem(id) {
  try { await api.delete(`/outros-itens/${id}`); dialogExcluirItem.value = false; await carregarItens() }
  catch (error) { notificarErro(error, 'excluir o item') }
}

onMounted(carregarItens)
</script>

<style scoped>
.outros-itens-page { padding: 24px; }
.outros-itens-lista { position: relative; min-height: 180px; }
</style>
