<template>
  <q-page class="page-container">
    <BensHeader v-model:filtros="filtros" :itens="bens" :opcoes="opcoes" @bem-salvo="bemSalvo" />

    <q-banner v-if="erro" class="bg-negative text-white q-mb-md" rounded>
      {{ erro }}
      <template #action>
        <q-btn flat color="white" label="Tentar novamente" @click="carregarBens" />
      </template>
    </q-banner>

    <BensTable
      :bens="bens"
      :opcoes="opcoes"
      :loading="carregando"
      @bens-atualizados="carregarBens"
    />
  </q-page>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { api } from '@/boot/axios'
import BensHeader from '@/components/bens/BensHeader.vue'
import BensTable from '@/components/bens/BensTable.vue'

const bens = ref([])
const opcoes = ref({ escritorios: [], departamentos: [], pessoas: [], categorias: [], status: [] })
const carregando = ref(false)
const erro = ref('')
const filtros = reactive({ busca: '', categoria: [], status: [], departamento: '' })
let timerBusca
let requestIdBens = 0

async function carregarOpcoes() {
  try {
    const { data } = await api.get('/bens/opcoes')
    opcoes.value = {
      escritorios: data.escritorios || [],
      departamentos: data.departamentos || [],
      pessoas: data.pessoas || [],
      categorias: data.categorias || [],
      status: data.status || [],
    }
  } catch (error) {
    if (error.response?.status !== 401) {
      erro.value = mensagemErro(error, 'as opções de cadastro')
    }
  }
}

async function carregarBens() {
  const chamadaAtual = ++requestIdBens
  carregando.value = true
  erro.value = ''
  try {
    const { data } = await api.get('/bens', {
      params: {
        busca: filtros.busca || undefined,
        categoria: filtros.categoria.length ? filtros.categoria.join(',') : undefined,
        status: filtros.status.length ? filtros.status.join(',') : undefined,
        departamento: filtros.departamento || undefined,
      },
    })
    if (chamadaAtual !== requestIdBens) return
    bens.value = data.map(mapearBem)
  } catch (error) {
    if (
      chamadaAtual !== requestIdBens ||
      error.code === 'ERR_CANCELED' ||
      error.response?.status === 401
    )
      return
    erro.value = mensagemErro(error, 'o inventário')
  } finally {
    if (chamadaAtual === requestIdBens) carregando.value = false
  }
}

function mensagemErro(error, alvo) {
  const status = error.response?.status
  if (!error.response)
    return `Servidor indisponível. Não foi possível carregar ${alvo}; tente novamente.`
  if (status === 403) return `Sua conta não tem permissão para carregar ${alvo}.`
  if (status >= 500)
    return `O servidor falhou ao carregar ${alvo} (HTTP ${status}). Tente novamente.`
  return error.response.data?.mensagem || `Não foi possível carregar ${alvo} (HTTP ${status}).`
}

async function atualizarInventario() {
  await Promise.all([carregarOpcoes(), carregarBens()])
}

async function bemSalvo() {
  clearTimeout(timerBusca)
  filtros.busca = ''
  filtros.categoria = []
  filtros.status = []
  filtros.departamento = ''
  await atualizarInventario()
}

function mapearBem(item) {
  const icons = {
    Veículo: ['agriculture', 'green-1', 'green-8'],
    Notebook: ['laptop', 'blue-1', 'blue-8'],
    Notebooks: ['laptop', 'blue-1', 'blue-8'],
    Monitor: ['monitor', 'teal-1', 'teal-8'],
    Monitores: ['monitor', 'teal-1', 'teal-8'],
    Impressora: ['print', 'orange-1', 'orange-8'],
    Impressoras: ['print', 'orange-1', 'orange-8'],
  }
  const [icon, avatarColor, iconColor] = icons[item.categoria] || [
    'inventory_2',
    'grey-3',
    'grey-8',
  ]
  return {
    ...item,
    id: item.id,
    descricao: item.nome,
    serie: item.serial,
    numeroSerie: item.serial,
    marca: [item.fabricante, item.modelo].filter(Boolean).join(' • '),
    escritorio: item.escritorio,
    localizacao: item.escritorio,
    responsavel: item.responsavel || 'Não atribuído',
    observacoes: item.descricao,
    icon,
    avatarColor,
    iconColor,
  }
}

watch(
  () => [filtros.busca, filtros.categoria, filtros.status, filtros.departamento],
  () => {
    clearTimeout(timerBusca)
    timerBusca = setTimeout(carregarBens, 250)
  },
  { deep: true },
)

onMounted(async () => {
  await atualizarInventario()
})
</script>
