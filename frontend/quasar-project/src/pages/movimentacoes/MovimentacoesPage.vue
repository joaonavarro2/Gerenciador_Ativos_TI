<template>
  <q-page class="movimentacoes-page">
    <MovimentacoesHeader
      :movimentacoes="movimentacoes"
      v-model:filtros="filtros"
    />

    <MovimentacoesTable :movimentacoes="movimentacoesFiltradas" />
  </q-page>
</template>

<script setup>
import { computed, ref } from 'vue'

import MovimentacoesHeader from '@/components/movimentacoes/MovimentacoesHeader.vue'
import MovimentacoesTable from '@/components/movimentacoes/MovimentacoesTable.vue'

// Fonte única dos dados da página. No futuro, esta lista será preenchida pela API.
const movimentacoes = ref([
  {
    id: 1,
    patrimonio: 'BA-021874',
    codigoMovimentacao: 'MOV-0045',
    bem: 'Impressora HP LaserJet Pro',
    tipo: 'Manutenção',
    tipoClasse: 'manutencao',
    tipoIcone: 'build',
    origem: 'Dep. de TI',
    destino: 'Assistência Técnica',
    data: '18/06/25',
    responsavel: 'Marcos Silva',
    iniciais: 'MS',
    status: 'Em Aberto',
    statusClasse: 'aberto',
    statusIcone: 'schedule',
  },
  {
    id: 2,
    patrimonio: 'BA-083401',
    codigoMovimentacao: 'MOV-0012',
    bem: 'Notebook Lenovo ThinkPad',
    tipo: 'Cessão',
    tipoClasse: 'cessao',
    tipoIcone: 'person_add',
    origem: 'Dep. Administrativo',
    destino: 'Prefeitura Municipal',
    data: '30/05/25',
    responsavel: 'Fernanda Rocha',
    iniciais: 'FR',
    status: 'Pendente',
    statusClasse: 'pendente',
    statusIcone: 'error_outline',
  },
  {
    id: 3,
    patrimonio: 'BA-042891',
    codigoMovimentacao: 'MOV-0001',
    bem: 'Notebook Dell Latitude 5540',
    tipo: 'Transferência',
    tipoClasse: 'transferencia',
    tipoIcone: 'sync_alt',
    origem: 'Dep. de TI',
    destino: 'Dep. Financeiro',
    data: '10/06/25',
    responsavel: 'João Matos',
    iniciais: 'JM',
    status: 'Concluído',
    statusClasse: 'concluido',
    statusIcone: 'check_circle_outline',
  },
])

// O estado dos filtros pertence à página, pois ele coordena Header e tabela.
const filtros = ref({
  busca: '',
  tipo: null,
  data: null,
})

const normalizarTexto = (valor) =>
  String(valor ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()

const movimentacoesFiltradas = computed(() => {
  const busca = normalizarTexto(filtros.value.busca)

  return movimentacoes.value.filter((movimentacao) => {
    const correspondeBusca = !busca || [
      movimentacao.patrimonio,
      movimentacao.codigoMovimentacao,
      movimentacao.bem,
      movimentacao.origem,
      movimentacao.destino,
      movimentacao.responsavel,
    ].some((campo) => normalizarTexto(campo).includes(busca))

    const correspondeTipo = !filtros.value.tipo || movimentacao.tipo === filtros.value.tipo
    const correspondeData = !filtros.value.data || movimentacao.data === filtros.value.data

    return correspondeBusca && correspondeTipo && correspondeData
  })
})

/*
 * BACKEND — SPRING BOOT
 *
 * Quando a API estiver disponível, a fonte única continuará sendo esta página:
 *
 * const response = await api.get('/movimentacoes', { params: filtros.value })
 * movimentacoes.value = response.data
 *
 * Header e tabela não precisam conhecer a API; eles continuam recebendo props.
 */
</script>
