<template>
  <BaseDialog
    :model-value="dialog"
    title="Excluir Registro de Reparo"
    subtitle="Esta ação remove o registro do sistema."
    icon="delete"
    icon-color="negative"
    icon-background="red-1"
    width="640px"
    :show-separator="true"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card-section class="q-pa-lg">
      <div class="warning-box">
        <q-icon name="warning" size="34px" color="warning" />

        <div>
          <div class="warning-title">
            Tem certeza que deseja excluir este registro?
          </div>

          <div class="warning-text">
            Depois da confirmação, o reparo será removido permanentemente do sistema.
          </div>
        </div>
      </div>

      <q-card flat bordered class="reparo-resumo q-mt-md">
        <div class="resumo-item">
          <span class="label">ID</span>
          <strong>{{ reparo.id || '--' }}</strong>
        </div>

        <div class="resumo-item">
          <span class="label">Ativo</span>
          <strong>{{ reparo.ativoId || '--' }}</strong>
        </div>

        <div class="resumo-item">
          <span class="label">Problema</span>
          <strong>{{ reparo.problema || '--' }}</strong>
        </div>

        <div class="resumo-item">
          <span class="label">Técnico</span>
          <strong>{{ reparo.tecnico || '--' }}</strong>
        </div>
      </q-card>
    </q-card-section>

    <template #footer>
      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="Cancelar" @click="fechar" />
        <q-btn
          color="negative"
          icon="delete"
          label="Excluir Registro"
          :disable="!reparo || !reparo.id"
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
  reparo: {
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
  if (!props.reparo || !props.reparo.id) return

  emit('excluir', props.reparo.id)
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

.reparo-resumo {
  border-radius: 14px;
  padding: 12px 14px;
}

.resumo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f2f4f7;
}

.resumo-item:last-child {
  border-bottom: none;
}

.label {
  color: #667085;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
</style>
