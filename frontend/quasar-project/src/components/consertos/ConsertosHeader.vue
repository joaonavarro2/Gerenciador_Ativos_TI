<template>
  <q-card flat bordered class="bens-header">

    <!-- Topo com título e botão no canto direito -->
    <div class="row items-start justify-between q-mb-lg">

      <div>
        <div class="text-h4 text-weight-bold">Reparos de Ativos</div>
        <div class="text-subtitle1 text-grey-7 q-mt-xs">Gerencie manutenções, reparos e intervenções técnicas.</div>
      </div>

      <div class="row q-gutter-sm">
        <q-btn @click="$emit('novo')" color="positive" icon="add" label="Novo Reparo" no-caps />
      </div>

    </div>

    <!-- Filtros dentro do mesmo card -->
    <div class="row q-col-gutter-md">

      <div class="col">
        <div class="field-label">Buscar</div>
        <q-input v-model="busca" outlined dense placeholder="Buscar por ID do ativo, técnico ou descrição do problema..." clearable class="historico-busca">
          <template #prepend><q-icon name="search" /></template>
        </q-input>
      </div>

      <div class="col-auto">
        <div class="field-label">Data Inicial</div>
        <q-input v-model="dataInicial" outlined dense placeholder="dd/mm/aaaa" class="historico-filtro historico-data" clearable>
          <template #prepend><q-icon name="event" /></template>
        </q-input>
      </div>

      <div class="col-auto" style="display:flex;align-items:center">
        <span class="consertos-ate">até</span>
      </div>

      <div class="col-auto">
        <div class="field-label">Data Final</div>
        <q-input v-model="dataFinal" outlined dense placeholder="dd/mm/aaaa" class="historico-filtro historico-data" clearable>
          <template #prepend><q-icon name="event" /></template>
        </q-input>
      </div>

    </div>

    <div class="row q-col-gutter-md q-mt-md">
      <div class="col-auto">
        <q-select v-model="tipo" outlined dense :options="tipos" clearable class="historico-filtro" label="Tipo de Reparo" />
      </div>

      <div class="col-auto">
        <q-select v-model="status" outlined dense :options="statusOptions" clearable class="historico-filtro" label="Status" />
      </div>

      <div class="col-auto">
        <q-select v-model="departamento" outlined dense :options="departamentos" clearable class="historico-filtro historico-filtro-departamento" label="Departamento" />
      </div>

      <div class="col-auto">
        <q-select v-model="escritorio" outlined dense :options="escritorios" clearable class="historico-filtro" label="Escritório" />
      </div>
    </div>

  </q-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  filtros: { type: Object, required: true }
})

const emit = defineEmits(['update:filtros', 'novo'])

const criarFiltro = (chave) => computed({
  get: () => props.filtros[chave],
  set: (valor) => emit('update:filtros', { ...props.filtros, [chave]: valor || null }),
})

const busca = criarFiltro('busca')
const tipo = criarFiltro('tipo')
const status = criarFiltro('status')
const escritorio = criarFiltro('escritorio')
const departamento = criarFiltro('departamento')
const dataInicial = criarFiltro('dataInicial')
const dataFinal = criarFiltro('dataFinal')

const tipos = ['Reparo', 'Substituição', 'Instalação']
const statusOptions = ['Pendente', 'Em andamento', 'Concluído']
const escritorios = ['Alagoinhas', 'Amargosa', 'Barreiras', 'Salvador', 'Vitória da Conquista']
const departamentos = ['Assessoria da Diretoria','Controle Interno','Diretoria Geral','Dep. Financeiro']
</script>

<style scoped>
.consertos-filtros-card { padding: 16px; }
.consertos-filtros-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; align-items: center; }
.consertos-datas-row { display: flex; align-items: center; gap: 8px; }
.consertos-ate { white-space: nowrap; }

@media (max-width: 800px) {
  .consertos-filtros-grid { grid-template-columns: 1fr; }
}

/* Deixar labels em negrito e permiti-las quebrar para evitar cortes */
.bens-header .q-field__label {
  font-weight: 700;
  white-space: normal;
}

.field-label {
  font-weight: 700;
  margin-bottom: 6px;
  font-size: 13px;
  color: #334155;
}
</style>
