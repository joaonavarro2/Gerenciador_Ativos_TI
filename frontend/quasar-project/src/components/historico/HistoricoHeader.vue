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
        <q-btn outline no-caps icon="file_download" label="Exportar Histórico" class="historico-exportar" />
        <q-btn unelevated no-caps icon="description" label="Gerar Relatório" class="historico-relatorio" />
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
        <q-input v-model="dataInicial" outlined dense placeholder="dd/mm/aaaa" class="historico-filtro historico-data" clearable>
          <template #prepend><q-icon name="event" /></template>
        </q-input>
        <span class="historico-ate">até</span>
        <q-input v-model="dataFinal" outlined dense placeholder="dd/mm/aaaa" class="historico-filtro historico-data" clearable>
          <template #prepend><q-icon name="event" /></template>
        </q-input>
      </div>
    </q-card>
  </section>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  filtros: { type: Object, required: true },
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

const eventos = ['Cadastro', 'Movimentação', 'Manutenção', 'Alteração de Status']
const escritorios = ['Alagoinhas', 'Amargosa', 'Barreiras', 'Salvador', 'Vitória da Conquista']
const departamentos = ['Assessoria da Diretoria', 'Controle Interno', 'Diretoria Geral', 'Dep. Financeiro']
const usuarios = ['Carlos Andrade', 'Mariana Silva', 'João Mendes', 'Fernanda Rocha']
</script>
