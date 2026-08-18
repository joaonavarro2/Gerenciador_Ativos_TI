<template>
  <q-page class="historico-page">
    <HistoricoHeader v-model:filtros="filtros" />

    <HistoricoTable :historicos="historicosFiltrados" :possui-filtros="possuiFiltros" />
  </q-page>
</template>

<script setup>
import { computed, ref } from 'vue'

import HistoricoHeader from '@/components/historico/HistoricoHeader.vue'
import HistoricoTable from '@/components/historico/HistoricoTable.vue'

// Fonte única dos registros. O estado vazio reproduz a tela de referência.
const historicos = ref([])

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

const historicosFiltrados = computed(() => {
  // BACKEND — SPRING BOOT: o GET /historicos poderá receber filtros.value como params.
  // Os filtros locais serão aplicados aqui enquanto a API ainda não estiver disponível.
  return historicos.value
})

/*
 * BACKEND — SPRING BOOT
 *
 * const response = await api.get('/historicos', { params: filtros.value })
 * historicos.value = response.data
 */
</script>
