<template>
  <q-card flat bordered class="bens-table escritorios-table-card">
    <div class="table-header">
      <div class="table-title">
        <div class="table-title-main">Lista de Escritórios</div>
        <div class="table-title-sub">{{ escritorios.length }} registro(s)</div>
      </div>

      <div class="table-last-update">
        <q-icon name="schedule" size="16px" />
        Atualizado agora
      </div>
    </div>

    <div v-if="!escritorios.length" class="historico-empty-state">
      <div class="historico-empty-icon"><q-icon name="apartment" size="34px" /></div>
      <h2>Nenhum escritório encontrado.</h2>
      <p>Os escritórios cadastrados aparecerão aqui após o filtro ser ajustado ou um novo registro for incluído.</p>
    </div>

    <q-table
      v-else
      flat
      :rows="escritorios"
      :columns="columns"
      row-key="id"
      separator="horizontal"
      class="bens-qtable "
      hide-bottom
    >
      <template #body-cell-status="props">
        <q-td :props="props">
          <q-chip dense :class="statusClass(props.value)">
            {{ props.value || '--' }}
          </q-chip>
        </q-td>
      </template>

      <template #body-cell-endereco="props">
        <q-td :props="props">
          {{ props.value || '--' }}
        </q-td>
      </template>

      <template #body-cell-acoes="props">
        <q-td :props="props" class="text-center">
          <q-btn flat round dense class="acao-btn" @click="emit('visualizar', props.row)"><q-icon name="visibility" size="18px" /></q-btn>
          <q-btn flat round dense class="acao-btn" @click="emit('editar', props.row)"><q-icon name="edit" size="18px" /></q-btn>
          <q-btn flat round dense class="acao-btn" @click="emit('excluir', props.row)"><q-icon name="delete" size="18px" /></q-btn>
        </q-td>
      </template>
    </q-table>
  </q-card>
</template>

<script setup>
defineProps({
  escritorios: {
    type: Array,
    default: () => [],
  },
  possuiFiltros: {
    type: Boolean,
    default: false,
  },
  filtros: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['visualizar', 'editar', 'excluir'])

const columns = [
  { name: 'nome', label: 'NOME', field: 'nome', align: 'left' },
  { name: 'cidade', label: 'CIDADE', field: 'cidade', align: 'left' },
  { name: 'estado', label: 'ESTADO', field: 'estado', align: 'center' },
  { name: 'endereco', label: 'ENDEREÇO', field: 'endereco', align: 'left' },
  { name: 'responsavel', label: 'RESPONSÁVEL', field: 'responsavel', align: 'left' },
  { name: 'status', label: 'STATUS', field: 'status', align: 'center' },
  { name: 'acoes', label: 'AÇÕES', field: 'acoes', align: 'center' },
]

function statusClass(status) {
  if (status === 'Ativo') return 'status-chip status-ativo'
  if (status === 'Em expansão') return 'status-chip status-em-expansao'
  if (status === 'Inativo') return 'status-chip status-inativo'
  return 'status-chip status-default'
}
</script>

<style scoped>
.escritorios-table-card {
  margin-top: 24px;
}

.acao-btn {
  margin: 0 2px;
}

.historico-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 350px;
  padding: 32px 20px;
  text-align: center;
}

.historico-empty-icon {
  width: 62px;
  height: 62px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eef2ff;
  color: #4338ca;
  margin-bottom: 16px;
}

.historico-empty-state h2 {
  margin: 0;
  color: #101828;
  font-size: 17px;
  font-weight: 700;
}

.historico-empty-state p {
  margin: 10px 0 0;
  color: #98a2b3;
  font-size: 14px;
  line-height: 1.55;
}

.status-chip {
  border-radius: 8px;
  font-weight: 600;
  padding: 0 10px;
}

.status-ativo {
  background: #ecfdf3;
  color: #027a48;
}

.status-em-expansao {
  background: #fff7ed;
  color: #c2410c;
}

.status-inativo {
  background: #f2f4f7;
  color: #475467;
}

.status-default {
  background: #eff6ff;
  color: #1d4ed8;
}
</style>
