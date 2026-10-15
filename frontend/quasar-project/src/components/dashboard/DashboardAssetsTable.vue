<template>
  <q-card flat class="dashboard-table">
    <div class="table-header">
      <div>
        <h2>Registro de Bens</h2>
        <span>Os cinco bens adquiridos mais recentemente</span>
      </div>

      <q-input v-model="busca" outlined dense placeholder="Buscar..." class="table-search">
        <template #prepend>
          <q-icon name="search" />
        </template>
      </q-input>
    </div>

    <q-table
      flat
      :rows="rows"
      :columns="columns"
      :filter="busca"
      row-key="id"
      hide-bottom
      separator="horizontal"
      class="assets-table"
    >
      <template #body-cell-status="props">
        <q-td :props="props">
          <q-badge rounded color="grey-3" text-color="grey-7">
            {{ props.value || '--' }}
          </q-badge>
        </q-td>
      </template>
    </q-table>
  </q-card>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  rows: {
    type: Array,
    default: () => [],
  },
})

const busca = ref('')

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left' },
  { name: 'descricao', label: 'DESCRIÇÃO', field: 'descricao', align: 'left' },
  { name: 'unidade', label: 'UNIDADE', field: 'unidade' },
  { name: 'departamento', label: 'DEPARTAMENTO', field: 'departamento' },
  { name: 'data', label: 'DATA', field: 'data' },
  { name: 'status', label: 'STATUS', field: 'status' },
]
</script>
