<template>
  <q-card flat bordered class="bens-table consertos-table">

    <div class="table-header">
      <div class="table-title">
        <div class="table-title-main">Registros de Reparo</div>
        <div class="table-title-sub">{{ consertos ? consertos.length : 0 }} registro(s)</div>
      </div>

      <div class="table-last-update">Atualizado agora</div>
    </div>

    <q-table flat class="bens-qtable" :rows="consertos" :columns="columns" row-key="id">
      <template #body="props">
        <q-tr :props="props">
          <q-td key="id" :props="props">
            <q-badge class="badge-id">{{ props.row.id }}</q-badge>
          </q-td>

          <q-td key="ativoId" :props="props">{{ props.row.ativoId }}</q-td>

          <q-td key="problema" :props="props">
            <div class="descricao-title">{{ props.row.problema }}</div>
            <div class="text-caption">{{ props.row.descricao }}</div>
          </q-td>

          <q-td key="tecnico" :props="props">{{ props.row.tecnico }}</q-td>

          <q-td key="status" :props="props">
            <q-chip dense :class="statusClass(props.row.status)">{{ props.row.status }}</q-chip>
          </q-td>

          <q-td key="data" :props="props">{{ props.row.data }}</q-td>

          <q-td key="acoes" :props="props" class="text-center">
            <q-btn flat round dense class="acao-btn"><q-icon name="visibility" size="18px" /></q-btn>
            <q-btn flat round dense class="acao-btn"><q-icon name="edit" size="18px" /></q-btn>
          </q-td>
        </q-tr>
      </template>
    </q-table>

    <div class="table-footer">
      <div class="footer-left">Exibindo {{ consertos ? consertos.length : 0 }} registros</div>
      <div class="footer-right">
        <q-chip dense class="status-pendente">Pendente</q-chip>
        <q-chip dense class="status-concluido">Concluído</q-chip>
      </div>
    </div>

  </q-card>
</template>

<script setup>
import { computed } from 'vue'

defineProps({
  consertos: { type: Array, default: () => [] },
  possuiFiltros: Boolean,
  filtros: Object,
})

const columns = computed(() => [
  { name: 'id', label: 'ID', field: 'id' },
  { name: 'ativoId', label: 'Ativo', field: 'ativoId' },
  { name: 'problema', label: 'Problema', field: 'problema' },
  { name: 'tecnico', label: 'Técnico', field: 'tecnico' },
  { name: 'status', label: 'Status', field: 'status' },
  { name: 'data', label: 'Data', field: 'data' },
  { name: 'acoes', label: 'Ações', field: 'acoes' },
])

function statusClass(status) {
  if (!status) return ''
  if (status.toLowerCase().includes('pend')) return 'status-pendente'
  if (status.toLowerCase().includes('concl')) return 'status-concluido'
  return ''
}
</script>

<style scoped>
.consertos-table .q-item { cursor: default; }
</style>
