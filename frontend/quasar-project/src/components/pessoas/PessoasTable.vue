<template>
  <q-card flat bordered class="bens-table pessoas-table-card">
    <div class="table-header">
      <div class="table-title">
        <div class="table-title-main">Colaboradores Cadastrados</div>
        <div class="table-title-sub">{{ pessoas.length }} registro(s)</div>
      </div>

      <div class="table-last-update">
        <q-icon name="schedule" size="16px" />
        Atualizado agora
      </div>
    </div>

    <div v-if="!pessoas.length" class="historico-empty-state pessoas-empty-state">
      <div class="historico-empty-icon pessoas-empty-icon">
        <q-icon name="person_off" size="34px" />
      </div>
      <h2>Nenhuma pessoa cadastrada ainda.</h2>
      <p>Clique em 'Nova Pessoa' para cadastrar o primeiro colaborador.</p>

      <q-btn
        color="positive"
        class="pessoas-empty-btn"
        icon="add"
        label="Nova Pessoa"
        no-caps
      />
    </div>

    <q-table
      v-else
      flat
      :rows="pessoas"
      :columns="columns"
      row-key="id"
      separator="horizontal"
      class="bens-qtable pessoas-qtable"
      hide-bottom
    >
      <template #body-cell-status="props">
        <q-td :props="props">
          <q-chip dense :class="statusClass(props.value)">
            {{ props.value || '--' }}
          </q-chip>
        </q-td>
      </template>

      <template #body-cell-acoes="props">
        <q-td :props="props" class="text-center">
          <q-btn flat round dense class="acao-btn" @click="emit('visualizar', props.row)">
            <q-icon name="visibility" size="18px" />
          </q-btn>
          <q-btn flat round dense class="acao-btn" @click="emit('editar', props.row)">
            <q-icon name="edit" size="18px" />
          </q-btn>
          <q-btn flat round dense class="acao-btn" @click="emit('excluir', props.row)">
            <q-icon name="delete" size="18px" />
          </q-btn>
        </q-td>
      </template>
    </q-table>
  </q-card>
</template>

<script setup>
defineProps({
  pessoas: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['visualizar', 'editar', 'excluir'])

const columns = [
  { name: 'nome', label: 'NOME COMPLETO', field: 'nome', align: 'left' },
  { name: 'cpf', label: 'CPF', field: 'cpf', align: 'left' },
  { name: 'email', label: 'E-MAIL', field: 'email', align: 'left' },
  { name: 'cargo', label: 'CARGO', field: 'cargo', align: 'left' },
  { name: 'escritorio', label: 'ESCRITÓRIO', field: 'escritorio', align: 'left' },
  { name: 'departamento', label: 'DEPARTAMENTO', field: 'departamento', align: 'left' },
  { name: 'bens', label: 'BENS', field: 'bens', align: 'center' },
  { name: 'status', label: 'STATUS', field: 'status', align: 'center' },
  { name: 'acoes', label: 'AÇÕES', field: 'acoes', align: 'center' },
]

function statusClass(status) {
  if (status === 'Ativo') return 'status-chip status-ativo'
  if (status === 'Inativo') return 'status-chip status-inativo'
  return 'status-chip status-default'
}
</script>
