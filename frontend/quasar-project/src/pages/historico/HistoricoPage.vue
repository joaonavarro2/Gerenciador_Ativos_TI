<template>
  <q-page class="historico-page">
    <HistoricoHeader v-model:filtros="filtros" :historicos="historicos" />

    <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative q-mb-md">
      {{ erro }}
      <template #action><q-btn flat color="negative" label="Tentar novamente" @click="carregarHistorico" /></template>
    </q-banner>
    <q-inner-loading :showing="carregando"><q-spinner size="40px" color="primary" /></q-inner-loading>

    <HistoricoTable :historicos="historicosFiltrados" :possui-filtros="possuiFiltros" :filtros="filtros" />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '@/boot/axios.js'
import HistoricoHeader from '@/components/historico/HistoricoHeader.vue'
import HistoricoTable from '@/components/historico/HistoricoTable.vue'

const historicos = ref([])
const carregando = ref(false)
const erro = ref('')
const filtros = ref({ busca: '', evento: null, escritorio: null, departamento: null, usuario: null, dataInicial: '', dataFinal: '' })

const nomeLocal = (...partes) => partes.filter(Boolean).join(' / ') || '—'
const timestamp = (valor) => valor ? new Date(valor).getTime() : 0

function carregarBens(registros) {
  return registros.map((item) => ({
    id: `bem-${item.id}`,
    ativoId: item.patrimonio || item.codigo || `Bem #${item.id}`,
    evento: 'Cadastro de bem',
    descricao: `${item.nome || 'Bem'} cadastrado${item.categoria ? ` na categoria ${item.categoria}` : ''}${item.responsavel ? `, responsável: ${item.responsavel}` : ''}.`,
    usuario: item.responsavel || '—',
    escritorio: item.escritorio || '—',
    departamento: item.departamento || '—',
    data: item.dataCadastro || item.dataAquisicao || null,
    tipoRegistro: 'Cadastro',
    detalhes: {
      'Patrimônio': item.patrimonio || '—', 'Código': item.codigo || '—', 'Bem': item.nome || '—',
      'Categoria': item.categoria || '—', 'Número de série': item.serial || '—', 'Fabricante': item.fabricante || '—',
      'Modelo': item.modelo || '—', 'Status': item.status || '—', 'Data de aquisição': item.dataAquisicao || '—',
      'Escritório': item.escritorio || '—', 'Departamento': item.departamento || '—', 'Responsável': item.responsavel || '—',
    },
  }))
}

function carregarMovimentacoes(registros) {
  return registros.map((item) => ({
    id: `mov-${item.id}`,
    ativoId: item.patrimonioBem || `Bem #${item.itemId}`,
    evento: 'Movimentação',
    descricao: `${item.tipo || 'Movimentação'}: ${nomeLocal(item.escritorioOrigem, item.departamentoOrigem, item.pessoaOrigem)} → ${nomeLocal(item.escritorioDestino, item.departamentoDestino, item.pessoaDestino)}${item.justificativa ? `. ${item.justificativa}` : ''}`,
    usuario: item.responsavel || '—',
    escritorio: item.escritorioDestino || item.escritorioOrigem || '—',
    departamento: item.departamentoDestino || item.departamentoOrigem || '—',
    data: item.data,
    tipoRegistro: 'Movimentação',
    detalhes: {
      'Tipo': item.tipo || '—', 'Bem': item.nomeBem || '—', 'Patrimônio': item.patrimonioBem || '—',
      'Status': item.status || '—', 'Data': item.data || '—', 'Escritório de origem': item.escritorioOrigem || '—',
      'Departamento de origem': item.departamentoOrigem || '—', 'Pessoa de origem': item.pessoaOrigem || '—',
      'Escritório de destino': item.escritorioDestino || '—', 'Departamento de destino': item.departamentoDestino || '—',
      'Pessoa de destino': item.pessoaDestino || '—', 'Responsável': item.responsavel || '—', 'Justificativa': item.justificativa || '—',
    },
  }))
}

function carregarConsertos(registros) {
  return registros.map((item) => ({
    id: `rep-${item.id}`,
    ativoId: item.patrimonioBem || item.codigoBem || `Bem #${item.itemId}`,
    evento: 'Reparo',
    descricao: `${item.tipo || 'Reparo'}: ${item.descricaoProblema || 'Problema não informado'}${item.descricaoSolucao ? ` Solução: ${item.descricaoSolucao}` : ''}${item.status ? ` (status: ${item.status})` : ''}`,
    usuario: item.tecnico || item.usuario || '—',
    escritorio: item.escritorio || '—',
    departamento: item.departamento || '—',
    data: item.dataSaida || item.dataEntrada,
    tipoRegistro: 'Reparo',
    detalhes: {
      'Tipo': item.tipo || '—', 'Bem': item.nomeBem || '—', 'Patrimônio': item.patrimonioBem || '—',
      'Problema': item.descricaoProblema || '—', 'Solução': item.descricaoSolucao || '—', 'Status': item.status || '—',
      'Data de entrada': item.dataEntrada || '—', 'Data de saída': item.dataSaida || '—',
      'Técnico': item.tecnico || '—', 'Usuário responsável': item.usuario || '—',
      'Escritório': item.escritorio || '—', 'Departamento': item.departamento || '—',
    },
  }))
}

async function carregarHistorico() {
  carregando.value = true
  erro.value = ''
  try {
    const resultados = await Promise.allSettled([
      api.get('/bens'),
      api.get('/movimentacoes'),
      api.get('/consertos'),
    ])
    const fontes = resultados.map((resultado) => resultado.status === 'fulfilled' ? resultado.value.data : null)
    const falhas = resultados.filter((resultado) => resultado.status === 'rejected')
    if (fontes.every((fonte) => fonte === null)) {
      throw falhas[0]?.reason || new Error('Não foi possível carregar o histórico.')
    }
    const [bens, movimentacoes, consertos] = fontes
    historicos.value = [
      ...(Array.isArray(bens) ? carregarBens(bens) : []),
      ...(Array.isArray(movimentacoes) ? carregarMovimentacoes(movimentacoes) : []),
      ...(Array.isArray(consertos) ? carregarConsertos(consertos) : []),
    ].sort((a, b) => timestamp(b.data) - timestamp(a.data))
    if (falhas.length) erro.value = 'Parte do histórico não pôde ser carregada. Os registros das fontes disponíveis estão sendo exibidos.'
  } catch (error) {
    erro.value = error.response?.data?.mensagem || 'Não foi possível carregar o histórico. Tente novamente.'
  } finally {
    carregando.value = false
  }
}

const normalizar = (valor) => String(valor ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').trim().toLocaleLowerCase('pt-BR')
const possuiFiltros = computed(() => Object.values(filtros.value).some(Boolean))
const historicosFiltrados = computed(() => {
  const f = filtros.value
  return historicos.value.filter((item) => {
    const busca = normalizar(f.busca)
    if (busca && !normalizar([item.ativoId, item.evento, item.descricao, item.usuario, item.escritorio, item.departamento].join(' ')).includes(busca)) return false
    if (f.evento && normalizar(item.evento) !== normalizar(f.evento)) return false
    if (f.escritorio && normalizar(item.escritorio) !== normalizar(f.escritorio)) return false
    if (f.departamento && normalizar(item.departamento) !== normalizar(f.departamento)) return false
    if (f.usuario && normalizar(item.usuario) !== normalizar(f.usuario)) return false
    const data = item.data ? new Date(item.data) : null
    if (f.dataInicial && (!data || data < new Date(`${f.dataInicial}T00:00:00`))) return false
    if (f.dataFinal && (!data || data > new Date(`${f.dataFinal}T23:59:59`))) return false
    return true
  })
})

onMounted(carregarHistorico)
</script>
