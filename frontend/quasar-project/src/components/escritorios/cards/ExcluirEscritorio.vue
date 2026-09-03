<template>
  <BaseDialog
    :model-value="dialog"
    title="Excluir Escritório"
    subtitle="Esta ação não pode ser desfeita."
    icon="delete"
    icon-color="negative"
    icon-background="red-1"
    width="640px"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card-section class="q-pa-lg">
      <div class="warning-box">
        <q-icon name="warning" size="34px" color="warning" />

        <div>
          <div class="warning-title">Tem certeza que deseja excluir este escritório?</div>
          <div class="warning-text">
            O registro será removido permanentemente e poderá afetar vínculos do sistema.
          </div>
        </div>
      </div>

      <q-card flat bordered class="resumo q-mt-md">
        <div class="resumo-item">
          <span class="label">Nome</span>
          <strong>{{ escritorio.nome || '--' }}</strong>
        </div>

        <div class="resumo-item">
          <span class="label">Cidade</span>
          <strong>{{ escritorio.cidade || '--' }}</strong>
        </div>

        <div class="resumo-item">
          <span class="label">Responsável</span>
          <strong>{{ escritorio.responsavel || '--' }}</strong>
        </div>
      </q-card>
    </q-card-section>

    <template #footer>
      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="Cancelar" @click="fechar" />
        <q-btn
          color="negative"
          icon="delete"
          label="Excluir Escritório"
          :disable="!escritorio || !escritorio.id"
          @click="confirmarExclusao"
        />
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
  escritorio: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:modelValue', 'excluir'])

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

function fechar() {
  emit('update:modelValue', false)
}

function confirmarExclusao() {
  if (!props.escritorio || !props.escritorio.id) return

  emit('excluir', props.escritorio.id)
  emit('update:modelValue', false)
}
</script>

<style scoped>
.warning-box {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 14px;
  background: #fff7ed;
  border: 1px solid #fed7aa;
}

.warning-title {
  font-size: 1.05rem;
  font-weight: 700;
  color: #1f2937;
}

.warning-text {
  margin-top: 4px;
  color: #475467;
  font-size: 0.92rem;
}

.resumo {
  border-radius: 14px;
}

.resumo-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 14px;
  border-bottom: 1px solid #f2f4f7;
}

.resumo-item:last-child {
  border-bottom: none;
}

.label {
  color: #667085;
  font-size: 0.74rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
</style>
