<template>
  <q-card flat bordered class="bens-table outros-itens-table-card">
    <div class="table-header">
      <div class="table-title">
        <div class="table-title-main">Inventário de Outros Itens</div>
        <div class="table-title-sub">{{ itens.length }} itens cadastrados</div>
      </div>

      <div class="table-last-update">
        <q-icon name="layers" size="16px" />
        {{ itens.length }} itens cadastrados
      </div>
    </div>

    <q-table
      v-if="itens.length"
      flat
      :rows="itens"
      :columns="columns"
      row-key="id"
      separator="horizontal"
      class="bens-qtable outros-itens-qtable"
      hide-bottom
    >
      <template #body-cell-status="slotProps">
        <q-td :props="slotProps">
          <q-chip dense :class="statusClass(slotProps.value)">
            {{ slotProps.value || '--' }}
          </q-chip>
        </q-td>
      </template>

      <template #body-cell-acoes="slotProps">
        <q-td :props="slotProps" class="text-center">
          <q-btn flat round dense class="acao-btn" icon="visibility" @click="emit('visualizar', slotProps.row)" />
          <q-btn flat round dense class="acao-btn" icon="edit" @click="emit('editar', slotProps.row)" />
          <q-btn flat round dense class="acao-btn" icon="delete" @click="emit('excluir', slotProps.row)" />
        </q-td>
      </template>
    </q-table>

    <div v-else class="historico-empty-state outros-itens-empty-state">
      <div class="historico-empty-icon outros-itens-empty-icon">
        <q-icon name="layers" size="34px" />
      </div>
      <h2>{{ possuiFiltros ? 'Nenhum item encontrado.' : 'Nenhum item cadastrado ainda.' }}</h2>
      <p>
        {{ possuiFiltros
          ? 'Tente ajustar os filtros para visualizar outros itens.'
          : "Clique em 'Novo Item' para registrar o primeiro item de estoque." }}
      </p>
    </div>
  </q-card>
</template>

<script setup>
defineProps({
  itens: {
    type: Array,
    default: () => [],
  },
  possuiFiltros: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['visualizar', 'editar', 'excluir'])

const columns = [
  { name: 'codigo', label: 'CÓDIGO', field: 'codigo', align: 'left' },
  { name: 'nome', label: 'NOME DO ITEM', field: 'nome', align: 'left' },
  { name: 'categoria', label: 'CATEGORIA', field: 'categoria', align: 'left' },
  { name: 'escritorio', label: 'ESCRITÓRIO', field: 'escritorio', align: 'left' },
  { name: 'localizacao', label: 'LOCALIZAÇÃO', field: 'localizacao', align: 'left' },
  { name: 'quantidade', label: 'QTD. DISPONÍVEL', field: 'quantidade', align: 'center' },
  { name: 'estoqueMinimo', label: 'EST. MÍNIMO', field: 'estoqueMinimo', align: 'center' },
  { name: 'fornecedor', label: 'FORNECEDOR', field: 'fornecedor', align: 'left' },
  { name: 'status', label: 'STATUS', field: 'status', align: 'center' },
  { name: 'acoes', label: 'AÇÕES', field: 'acoes', align: 'center' },
]

function statusClass(status) {
  if (status === 'Ativo') return 'status-chip status-ativo'
  if (status === 'Inativo') return 'status-chip status-inativo'
  return 'status-chip status-default'
}
</script>
