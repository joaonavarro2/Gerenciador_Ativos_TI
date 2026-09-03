<template>
  <q-page class="outros-itens-page">
    <HeaderOutros
      v-model:filtros="filtros"
      @novo-item="abrirNovoItem"
    />

    <TableOutros
      :itens="itensFiltrados"
      :possui-filtros="possuiFiltros"
      @visualizar="visualizarItem"
      @editar="editarItem"
      @excluir="excluirItem"
    />

    <NovoItem v-model="dialogNovoItem" @salvar="adicionarItem" />
    <VisualizarItem v-model="dialogVisualizarItem" :item="itemSelecionado" />
    <EditarItem v-model="dialogEditarItem" :item="itemSelecionado" @salvar="atualizarItem" />
    <ExcluirItem v-model="dialogExcluirItem" :item="itemSelecionado" @excluir="removerItem" />
  </q-page>
</template>

<script setup>
import { computed, ref } from 'vue'

import HeaderOutros from '@/components/outrosItens/HeaderOutros.vue'
import TableOutros from '@/components/outrosItens/TableOutros.vue'
import NovoItem from '@/components/outrosItens/card/NovoItem.vue'
import VisualizarItem from '@/components/outrosItens/card/VisualizarItem.vue'
import EditarItem from '@/components/outrosItens/card/EditarItem.vue'
import ExcluirItem from '@/components/outrosItens/card/ExcluirItem.vue'

const itens = ref([
  {
    id: 1,
    codigo: 'OUT-001',
    nome: 'Papel A4 - Resma',
    categoria: 'Material de escritório',
    escritorio: 'Salvador',
    localizacao: 'Almoxarifado Central',
    quantidade: 48,
    estoqueMinimo: 20,
    fornecedor: 'Papelaria Bahia',
    status: 'Ativo',
  },
  {
    id: 2,
    codigo: 'OUT-002',
    nome: 'Toner HP 85A',
    categoria: 'Suprimentos',
    escritorio: 'Salvador',
    localizacao: 'Sala de suprimentos',
    quantidade: 12,
    estoqueMinimo: 8,
    fornecedor: 'SupriTech',
    status: 'Ativo',
  },
  {
    id: 3,
    codigo: 'OUT-003',
    nome: 'Cadeira Giratória',
    categoria: 'Mobiliário',
    escritorio: 'Feira de Santana',
    localizacao: 'Depósito administrativo',
    quantidade: 4,
    estoqueMinimo: 2,
    fornecedor: 'Móveis Office',
    status: 'Ativo',
  },
  {
    id: 4,
    codigo: 'OUT-004',
    nome: 'Caneta Esferográfica Azul',
    categoria: 'Material de escritório',
    escritorio: 'Vitória da Conquista',
    localizacao: 'Almoxarifado local',
    quantidade: 0,
    estoqueMinimo: 30,
    fornecedor: 'Papelaria Bahia',
    status: 'Esgotado',
  },
  {
    id: 5,
    codigo: 'OUT-005',
    nome: 'Cabo HDMI 2 metros',
    categoria: 'Outros',
    escritorio: 'Salvador',
    localizacao: 'Armário de tecnologia',
    quantidade: 7,
    estoqueMinimo: 3,
    fornecedor: 'Conecta Comércio',
    status: 'Ativo',
  },
  {
    id: 6,
    codigo: 'OUT-006',
    nome: 'Pasta Suspensa',
    categoria: 'Material de escritório',
    escritorio: 'Feira de Santana',
    localizacao: 'Arquivo geral',
    quantidade: 9,
    estoqueMinimo: 15,
    fornecedor: 'Office Center',
    status: 'Inativo',
  },
])

const dialogNovoItem = ref(false)
const dialogVisualizarItem = ref(false)
const dialogEditarItem = ref(false)
const dialogExcluirItem = ref(false)
const itemSelecionado = ref({})

const filtros = ref({
  busca: '',
  categoria: null,
  escritorio: null,
  status: null,
})

const possuiFiltros = computed(() =>
  Object.values(filtros.value).some(value => value !== null && value !== ''),
)

const itensFiltrados = computed(() => {
  const filtro = filtros.value
  const busca = filtro.busca.trim().toLowerCase()

  return itens.value.filter(item => {
    const correspondeBusca = !busca || [
      item.codigo,
      item.nome,
      item.categoria,
      item.escritorio,
      item.localizacao,
      item.fornecedor,
    ].some(valor => String(valor || '').toLowerCase().includes(busca))

    return correspondeBusca
      && (!filtro.categoria || item.categoria === filtro.categoria)
      && (!filtro.escritorio || item.escritorio === filtro.escritorio)
      && (!filtro.status || item.status === filtro.status)
  })
})

function abrirNovoItem() {
  dialogNovoItem.value = true
}

function visualizarItem(item) {
  itemSelecionado.value = item
  dialogVisualizarItem.value = true
}

function editarItem(item) {
  itemSelecionado.value = item
  dialogEditarItem.value = true
}

function excluirItem(item) {
  itemSelecionado.value = item
  dialogExcluirItem.value = true
}

function adicionarItem(item) {
  itens.value.unshift(item)
}

function atualizarItem(itemAtualizado) {
  itens.value = itens.value.map(item =>
    item.id === itemAtualizado.id ? { ...item, ...itemAtualizado } : item,
  )
}

function removerItem(id) {
  itens.value = itens.value.filter(item => item.id !== id)
}
</script>
