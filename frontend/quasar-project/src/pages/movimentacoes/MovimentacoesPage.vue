<template>
  <q-page class="movimentacoes-page">
    <MovimentacoesHeader :movimentacoes="movimentacoes" v-model:filtros="filtros" @registrar="carregarMovimentacoes" />
    <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative q-mb-md">{{ erro }}</q-banner>
    <q-inner-loading :showing="carregando"><q-spinner size="40px" color="primary" /></q-inner-loading>
    <MovimentacoesTable :movimentacoes="movimentacoesFiltradas" />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import MovimentacoesHeader from '@/components/movimentacoes/MovimentacoesHeader.vue'
import MovimentacoesTable from '@/components/movimentacoes/MovimentacoesTable.vue'
import { api } from '@/boot/axios.js'

const movimentacoes = ref([])
const carregando = ref(false)
const erro = ref('')
const filtros = ref({ busca: '', tipo: null, data: null })

const normalizar = (valor) => String(valor ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR')
const formatarData = (valor) => valor ? new Intl.DateTimeFormat('pt-BR').format(new Date(valor)) : ''
const nomeLocal = (escritorio, departamento, pessoa) => [escritorio, departamento, pessoa].filter(Boolean).join(' / ') || '—'
const mapear = (m) => {
  const status = normalizar(m.status).includes('conclu') ? 'Concluído' : (m.status || 'Em Aberto')
  const origem = nomeLocal(m.escritorioOrigem, m.departamentoOrigem, m.pessoaOrigem)
  const destino = nomeLocal(m.escritorioDestino, m.departamentoDestino, m.pessoaDestino)
  const responsavel = m.responsavel || '—'
  const iniciais = responsavel.split(/\s+/).slice(0, 2).map((parte) => parte[0]).join('').toLocaleUpperCase('pt-BR')
  const tipoClasse = normalizar(m.tipo).replace(/\s+/g, '-')
  const statusClasse = normalizar(status).replace(/\s+/g, '-')
  return {
    ...m,
    patrimonio: m.patrimonioBem || `Bem #${m.itemId}`,
    codigoMovimentacao: `MOV-${String(m.id).padStart(4, '0')}`,
    bem: m.nomeBem || `Bem #${m.itemId}`,
    tipoClasse,
    tipoIcone: m.tipo?.toLocaleLowerCase('pt-BR').includes('devol') ? 'assignment_return' : 'sync_alt',
    origem,
    destino,
    data: formatarData(m.data),
    dataIso: m.data?.slice(0, 10) || '',
    responsavel,
    iniciais: iniciais || '—',
    statusClasse,
    statusIcone: normalizar(status).includes('conclu') ? 'check_circle_outline' : 'schedule',
  }
}

async function carregarMovimentacoes() {
  carregando.value = true
  erro.value = ''
  try {
    const { data } = await api.get('/movimentacoes')
    movimentacoes.value = data.map(mapear)
  } catch (e) {
    erro.value = e.response?.data?.mensagem || 'Não foi possível carregar as movimentações.'
  } finally { carregando.value = false }
}

const movimentacoesFiltradas = computed(() => {
  const busca = normalizar(filtros.value.busca)
  return movimentacoes.value.filter((m) => {
    const texto = [m.patrimonio, m.codigoMovimentacao, m.bem, m.origem, m.destino, m.responsavel].some((campo) => normalizar(campo).includes(busca))
    const tipo = !filtros.value.tipo || m.tipo === filtros.value.tipo
    const filtroData = filtros.value.data || ''
    const data = !filtroData || m.dataIso === filtroData || m.data.startsWith(filtroData)
    return texto && tipo && data
  })
})

onMounted(carregarMovimentacoes)
</script>
