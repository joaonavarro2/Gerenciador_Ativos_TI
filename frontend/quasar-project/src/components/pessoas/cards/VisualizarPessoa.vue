<template>
  <BaseDialog
    :model-value="dialog"
    :title="pessoa.nome || 'Detalhes da Pessoa'"
    subtitle="Informações do colaborador cadastrado."
    icon="person"
    icon-color="primary"
    icon-background="indigo-1"
    width="760px"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card-section class="q-pa-lg">
      <div class="pessoa-info-grid">
        <div class="pessoa-info-item">
          <span class="label">Matrícula</span>
          <strong>{{ pessoa.matricula || '--' }}</strong>
        </div>

        <div class="pessoa-info-item">
          <span class="label">Nome</span>
          <strong>{{ pessoa.nome || '--' }}</strong>
        </div>

        <div class="pessoa-info-item">
          <span class="label">CPF</span>
          <strong>{{ pessoa.cpf || '--' }}</strong>
        </div>

        <div class="pessoa-info-item">
          <span class="label">E-mail</span>
          <strong>{{ pessoa.email || '--' }}</strong>
        </div>

        <div class="pessoa-info-item">
          <span class="label">Cargo</span>
          <strong>{{ pessoa.cargo || '--' }}</strong>
        </div>

        <div class="pessoa-info-item">
          <span class="label">Status</span>
          <q-chip dense :class="statusClass(pessoa.status)">
            {{ pessoa.status || '--' }}
          </q-chip>
        </div>

        <div class="pessoa-info-item">
          <span class="label">Escritório</span>
          <strong>{{ pessoa.escritorio || '--' }}</strong>
        </div>

        <div class="pessoa-info-item">
          <span class="label">Departamento</span>
          <strong>{{ pessoa.departamento || '--' }}</strong>
        </div>
      </div>
    </q-card-section>

    <template #footer>
      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="Fechar" @click="emit('update:modelValue', false)" />
      </q-card-actions>
    </template>
  </BaseDialog>
</template>

<script setup>
import { computed } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  pessoa: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:modelValue'])

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

function statusClass(status) {
  if (status === 'Ativo') return 'status-chip status-ativo'
  if (status === 'Inativo') return 'status-chip status-inativo'
  return 'status-chip status-default'
}
</script>

<style scoped>
.pessoa-info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px 24px;
}

.pessoa-info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.label {
  color: #667085;
  font-size: 0.74rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-chip {
  width: fit-content;
  border-radius: 8px;
  font-weight: 600;
  padding: 0 10px;
}

.status-ativo {
  background: #ecfdf3;
  color: #027a48;
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
