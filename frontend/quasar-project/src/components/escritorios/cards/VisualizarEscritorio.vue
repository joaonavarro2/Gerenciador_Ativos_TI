<template>
  <BaseDialog
    :model-value="modelValue"
    title="Detalhes do Escritório"
    :subtitle="escritorio.nome || 'Informações do escritório'"
    icon="apartment"
    icon-color="primary"
    icon-background="indigo-1"
    width="760px"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <q-card-section class="q-pa-lg">
      <div class="info-grid">
        <div class="info-item">
          <span class="label">Nome</span>
          <strong>{{ escritorio.nome || '--' }}</strong>
        </div>

        <div class="info-item">
          <span class="label">Cidade</span>
          <strong>{{ escritorio.cidade || '--' }}</strong>
        </div>

        <div class="info-item">
          <span class="label">Estado</span>
          <strong>{{ escritorio.estado || '--' }}</strong>
        </div>

        <div class="info-item">
          <span class="label">Status</span>
          <q-chip dense :class="statusClass(escritorio.status)">
            {{ escritorio.status || '--' }}
          </q-chip>
        </div>

        <div class="info-item full-width">
          <span class="label">Endereço</span>
          <strong>{{ escritorio.endereco || '--' }}</strong>
        </div>

        <div class="info-item">
          <span class="label">Responsável</span>
          <strong>{{ escritorio.responsavel || '--' }}</strong>
        </div>

        <div class="info-item">
          <span class="label">E-mail</span>
          <strong>{{ escritorio.email || '--' }}</strong>
        </div>

        <div class="info-item">
          <span class="label">Telefone</span>
          <strong>{{ escritorio.telefone || '--' }}</strong>
        </div>

        <div class="info-item">
          <span class="label">CEP</span>
          <strong>{{ escritorio.cep || '--' }}</strong>
        </div>
      </div>
    </q-card-section>

    <template #footer>
      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="Fechar" @click="$emit('update:modelValue', false)" />
      </q-card-actions>
    </template>
  </BaseDialog>
</template>

<script setup>
import BaseDialog from '@/components/common/BaseDialog.vue'

defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  escritorio: {
    type: Object,
    default: () => ({}),
  },
})

function statusClass(status) {
  if (status === 'Ativo') return 'status-chip status-ativo'
  if (status === 'Em expansão') return 'status-chip status-em-expansao'
  if (status === 'Inativo') return 'status-chip status-inativo'
  return 'status-chip status-default'
}
</script>

<style scoped>
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item.full-width {
  grid-column: 1 / -1;
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
