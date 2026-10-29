<template>
  <BaseDialog
    :model-value="dialog"
    title="Novo Departamento"
    subtitle="Cadastre um novo departamento organizacional."
    icon="business"
    icon-color="amber-9"
    icon-background="amber-1"
    width="720px"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-form class="q-pa-lg" @submit.prevent="salvar">
      <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative q-mb-md">{{ erro }}</q-banner>
      <div class="form-stack">
        <div class="field">
          <label class="label">
            Nome do Departamento
            <span class="required">*</span>
          </label>

          <q-input
            v-model="form.nome"
            outlined
            dense
            placeholder="Digite o nome do departamento"
            class="departamento-input"
          />
        </div>

        <div class="field">
          <label class="label">Escritório</label>

          <q-select
            v-model="form.escritorio"
            outlined
            dense
            clearable
            :options="escritorios"
            label="Selecione o escritório"
            option-label="label"
            option-value="value"
            emit-value
            map-options
            class="departamento-input"
          />
        </div>

        <div class="field">
          <label class="label">Status</label>

          <q-select
            v-model="form.status"
            outlined
            dense
            :options="statusOptions"
            class="departamento-input"
          />
        </div>
      </div>

      <div class="footer-actions">
        <q-btn flat color="grey-8" label="Cancelar" no-caps @click="fechar" />
        <q-btn color="positive" icon="check_circle" label="Cadastrar Departamento" no-caps type="submit" :loading="salvando" :disable="salvando" />
      </div>
    </q-form>
  </BaseDialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  escritorios: { type: Array, default: () => [] },
  salvando: { type: Boolean, default: false },
  erro: { type: String, default: '' },
})

const emit = defineEmits(['update:modelValue', 'salvar'])

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const form = ref({
  nome: '',
  escritorioId: null,
  status: 'Ativo',
})

const statusOptions = ['Ativo', 'Inativo']

watch(() => props.modelValue, (aberto, anterior) => {
  if (aberto && !anterior) form.value = { nome: '', escritorioId: null, status: 'Ativo' }
})

function fechar() {
  emit('update:modelValue', false)
}

function salvar() {
  if (!form.value.nome?.trim() || props.salvando) return

  emit('salvar', {
    ...form.value,
    sigla: form.value.nome
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(item => item[0])
      .join('')
      .toUpperCase(),
  })

}
</script>

<style scoped>
.form-stack {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label {
  font-size: 13px;
  font-weight: 700;
  color: #344054;
}

.required {
  color: #d92d20;
}

.departamento-input :deep(.q-field__control) {
  min-height: 42px;
  border-radius: 10px;
}

.footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>
