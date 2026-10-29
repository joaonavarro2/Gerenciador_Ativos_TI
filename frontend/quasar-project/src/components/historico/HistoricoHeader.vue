<template>
  <section class="historico-header">
    <div class="historico-page-top">
      <div>
        <h1 class="historico-page-title">Histórico de Ativos</h1>
        <p class="historico-page-subtitle">
          Linha do tempo completa de eventos realizados nos ativos organizacionais.
        </p>
      </div>

      <div class="historico-page-actions">
        <q-btn @click="dialogExportar = true" outline no-caps icon="file_download" label="Exportar Histórico" class="historico-exportar" />
        <ExportarRelatorio v-model="dialogExportar" :filtros="filtrosExportacao" :itens="itensExportacao" />
      </div>
    </div>

    <q-card flat bordered class="historico-filtros-card">
      <q-input
        v-model="busca"
        outlined
        dense
        placeholder="Pesquisar por ID do ativo, número de série, descrição, responsável ou evento..."
        class="historico-busca"
      >
        <template #prepend><q-icon name="search" /></template>
      </q-input>

      <div class="historico-filtros-grid">
        <q-select v-model="evento" outlined dense :options="eventos" label="Todos os Eventos" class="historico-filtro" clearable />
        <q-select v-model="escritorio" outlined dense :options="escritorios" label="Todos os Escritórios" class="historico-filtro" clearable />
        <q-select v-model="departamento" outlined dense :options="departamentos" label="Todos os Departamentos" class="historico-filtro historico-filtro-departamento" clearable />
        <q-select v-model="usuario" outlined dense :options="usuarios" label="Todos os Usuários" class="historico-filtro" clearable />

        <div class="historico-datas-row">
          <q-input v-model="dataInicial" outlined dense placeholder="dd/mm/aaaa" class="historico-filtro historico-data" clearable>
            <template #prepend><q-icon name="event" /></template>
          </q-input>

          <span class="historico-ate">até</span>

          <q-input v-model="dataFinal" outlined dense placeholder="dd/mm/aaaa" class="historico-filtro historico-data" clearable>
            <template #prepend><q-icon name="event" /></template>
          </q-input>
        </div>
      </div>
    </q-card>
  </section>
</template>

<script setup>
import { computed, ref } from 'vue'
import ExportarRelatorio from '@/components/common/ExportarRelatorio.vue'

const props = defineProps({
  filtros: { type: Object, required: true },
  historicos: { type: Array, default: () => [] },
})

const emit = defineEmits(['update:filtros'])

const criarFiltro = (chave) => computed({
  get: () => props.filtros[chave],
  set: (valor) => emit('update:filtros', { ...props.filtros, [chave]: valor || null }),
})

const busca = criarFiltro('busca')
const evento = criarFiltro('evento')
const escritorio = criarFiltro('escritorio')
const departamento = criarFiltro('departamento')
const usuario = criarFiltro('usuario')
const dataInicial = criarFiltro('dataInicial')
const dataFinal = criarFiltro('dataFinal')

const opcoesUnicas = (chave) => [...new Set(props.historicos.map((item) => item[chave]).filter((valor) => valor && valor !== '—'))].sort((a, b) => a.localeCompare(b, 'pt-BR'))
const eventos = computed(() => opcoesUnicas('evento'))
const escritorios = computed(() => opcoesUnicas('escritorio'))
const departamentos = computed(() => opcoesUnicas('departamento'))
const usuarios = computed(() => opcoesUnicas('usuario'))
const itensExportacao = computed(() => props.historicos.map((item) => ({
  id: item.ativoId,
  descricao: `${item.evento}: ${item.descricao}`,
  categoria: item.evento,
  status: item.tipoRegistro,
  departamento: item.departamento,
  responsavel: item.usuario,
})))
const filtrosExportacao = computed(() => ({
  busca: props.filtros.busca,
  categoria: props.filtros.evento ? [props.filtros.evento] : [],
  status: props.filtros.usuario ? [props.filtros.usuario] : [],
  departamento: props.filtros.departamento || '',
}))

const dialogExportar = ref(false)
</script>
