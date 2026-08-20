<template>
  <q-page class="historico-page">
    <HistoricoHeader v-model:filtros="filtros" />

    <HistoricoTable :historicos="historicosFiltrados" :possui-filtros="possuiFiltros" :filtros="filtros" />
  </q-page>
</template>

<script setup>
import { computed, ref } from 'vue'

import HistoricoHeader from '@/components/historico/HistoricoHeader.vue'
import HistoricoTable from '@/components/historico/HistoricoTable.vue'

// Fonte única dos registros. Adicionamos mocks para facilitar desenvolvimento.
// TODO: REMOVER MOCKS quando a API do backend estiver disponível. Substituir por chamada a
//   api.get('/historicos', { params: filtros.value }) e popular `historicos` com a resposta.
const historicos = ref([
  {
    id: 1,
    ativoId: 'ATV-1001',
    evento: 'Cadastro',
    descricao: 'Bem cadastrado no sistema',
    usuario: 'Carlos Andrade',
    escritorio: 'Salvador',
    departamento: 'Diretoria Geral',
    data: '2024-01-10',
  },
  {
    id: 2,
    ativoId: 'ATV-1002',
    evento: 'Movimentação',
    descricao: 'Movimentado para outro escritório',
    usuario: 'Mariana Silva',
    escritorio: 'Vitória da Conquista',
    departamento: 'Dep. Financeiro',
    data: '2024-03-22',
  },
  {
    id: 3,
    ativoId: 'ATV-1003',
    evento: 'Manutenção',
    descricao: 'Manutenção preventiva realizada',
    usuario: 'João Mendes',
    escritorio: 'Barreiras',
    departamento: 'Controle Interno',
    data: '2024-05-05',
  },
  {
    id: 4,
    ativoId: 'ATV-1004',
    evento: 'Alteração de Status',
    descricao: 'Status alterado para inativo',
    usuario: 'Fernanda Rocha',
    escritorio: 'Salvador',
    departamento: 'Dep. Financeiro',
    data: '2024-06-18',
  },
  {
    id: 5,
    ativoId: 'ATV-1005',
    evento: 'Movimentação',
    descricao: 'Transferido para Vitória da Conquista',
    usuario: 'Carlos Andrade',
    escritorio: 'Vitória da Conquista',
    departamento: 'Assessoria da Diretoria',
    data: '2024-07-02',
  },
])

const filtros = ref({
  busca: '',
  evento: null,
  escritorio: null,
  departamento: null,
  usuario: null,
  dataInicial: '',
  dataFinal: '',
})

const possuiFiltros = computed(() => Object.values(filtros.value).some(Boolean))

function texto(valor) {
  if (valor === null || valor === undefined) return ''
  if (Array.isArray(valor)) return valor.map(v => texto(v)).join(', ')
  if (typeof valor === 'object') return valor.nome || valor.label || valor.name || valor.value || ''
  return String(valor)
}

function normalizar(valor) {
  return texto(valor).trim().toLowerCase()
}

const historicosFiltrados = computed(() => {
  const f = filtros.value
  return historicos.value.filter(item => {
    // busca livre (procura em ativoId, descrição e usuário)
    if (f.busca) {
      const b = normalizar(f.busca)
      const combinado = `${item.ativoId} ${item.descricao} ${item.usuario}`
      if (!normalizar(combinado).includes(b)) return false
    }

    if (f.evento && String(f.evento).trim() !== '') {
      if (normalizar(item.evento) !== normalizar(f.evento)) return false
    }

    if (f.escritorio && String(f.escritorio).trim() !== '') {
      if (normalizar(item.escritorio) !== normalizar(f.escritorio)) return false
    }

    if (f.departamento && String(f.departamento).trim() !== '') {
      if (normalizar(item.departamento) !== normalizar(f.departamento)) return false
    }

    if (f.usuario && String(f.usuario).trim() !== '') {
      if (normalizar(item.usuario) !== normalizar(f.usuario)) return false
    }

    // filtro por período (se aplicável)
    if (f.dataInicial) {
      if (new Date(item.data) < new Date(f.dataInicial)) return false
    }
    if (f.dataFinal) {
      if (new Date(item.data) > new Date(f.dataFinal)) return false
    }

    return true
  })
})

/*
 * BACKEND — SPRING BOOT
 *
 * const response = await api.get('/historicos', { params: filtros.value })
 * historicos.value = response.data
 */
</script>
