<template>
  <q-dialog
    v-model="dialog"
    persistent
    transition-show="fade"
    transition-hide="fade"
  >
    <q-card class="escritorio-cadastro-dialog" style="width: 800px; max-width: 90vw; max-height: 120vh">
      <div class="escritorio-cadastro-header">
        <div class="escritorio-cadastro-titulo-area">
          <div class="escritorio-cadastro-titulo">
            Editar Escritório
          </div>

          <div class="escritorio-cadastro-subtitulo">
            Atualize os dados do escritório.
          </div>
        </div>

        <q-btn
          flat
          round
          dense
          icon="close"
          class="escritorio-cadastro-fechar"
          @click="fechar"
        />
      </div>

      <q-separator />

      <div class="escritorio-cadastro-content">
        <div class="escritorio-form">
          <div class="escritorio-form-group">
            <label class="escritorio-form-label">
              Nome do Escritório
              <span class="campo-obrigatorio">*</span>
            </label>

            <q-input
              v-model="form.nome"
              outlined
              dense
              placeholder="Ex: Filial Feira de Santana"
              class="escritorio-input"
            />
          </div>

          <div class="escritorio-form-row">
            <div class="escritorio-form-group">
              <label class="escritorio-form-label">
                Cidade
                <span class="campo-obrigatorio">*</span>
              </label>

              <q-input
                v-model="form.cidade"
                outlined
                dense
                placeholder="Ex: Feira de Santana"
                class="escritorio-input"
              />
            </div>

            <div class="escritorio-form-group">
              <label class="escritorio-form-label">
                CEP
              </label>

              <q-input
                v-model="form.cep"
                outlined
                dense
                placeholder="00000-000"
                class="escritorio-input"
              />
            </div>
          </div>

          <div class="escritorio-form-group">
            <label class="escritorio-form-label">
              Endereço
            </label>

            <q-input
              v-model="form.endereco"
              outlined
              dense
              placeholder="Rua, número, bairro"
              class="escritorio-input"
            />
          </div>

          <div class="escritorio-form-row">
            <div class="escritorio-form-group">
              <label class="escritorio-form-label">
                Telefone
              </label>

              <q-input
                v-model="form.telefone"
                outlined
                dense
                placeholder="(71) 3000-0000"
                class="escritorio-input"
              />
            </div>

            <div class="escritorio-form-group">
              <label class="escritorio-form-label">
                E-mail
              </label>

              <q-input
                v-model="form.email"
                outlined
                dense
                placeholder="filial@car.ba.gov.br"
                class="escritorio-input"
              />
            </div>
          </div>

          <div class="escritorio-form-row">
            <div class="escritorio-form-group">
              <label class="escritorio-form-label">
                Gestor Responsável
              </label>

              <q-input
                v-model="form.gestor"
                outlined
                dense
                placeholder="Nome do responsável"
                class="escritorio-input"
              />
            </div>

            <div class="escritorio-form-group">
              <label class="escritorio-form-label">
                Status
              </label>

              <q-select
                v-model="form.status"
                outlined
                dense
                :options="statusOptions"
                emit-value
                map-options
                class="escritorio-input"
              />
            </div>
          </div>
        </div>
      </div>

      <q-separator />

      <div class="escritorio-cadastro-footer">
        <q-btn
          outline
          color="grey-7"
          label="Cancelar"
          no-caps
          class="escritorio-btn-cancelar"
          @click="fechar"
        />

        <q-btn
          unelevated
          color="positive"
          label="Salvar alterações"
          no-caps
          class="escritorio-btn-salvar"
          @click="salvar"
        />
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

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

const emit = defineEmits(['update:modelValue', 'salvar'])

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const statusOptions = [
  { label: 'Ativo', value: 'Ativo' },
  { label: 'Em expansão', value: 'Em expansão' },
  { label: 'Inativo', value: 'Inativo' },
]

const form = ref({
  id: null,
  nome: '',
  cidade: '',
  cep: '',
  endereco: '',
  telefone: '',
  email: '',
  gestor: '',
  status: 'Ativo',
})

watch(
  () => props.escritorio,
  (value) => {
    form.value = {
      id: value?.id || null,
      nome: value?.nome || '',
      cidade: value?.cidade || '',
      cep: value?.cep || '',
      endereco: value?.endereco || '',
      telefone: value?.telefone || '',
      email: value?.email || '',
      gestor: value?.responsavel || '',
      status: value?.status || 'Ativo',
    }
  },
  { immediate: true, deep: true },
)

function fechar() {
  emit('update:modelValue', false)
}

function salvar() {
  if (!form.value.nome || !form.value.cidade) {
    return
  }

  emit('salvar', {
    ...props.escritorio,
    ...form.value,
    responsavel: form.value.gestor,
  })

  emit('update:modelValue', false)
}
</script>

<style scoped>
.escritorio-cadastro-dialog {
  width: 800px;
  max-width: 90vw;
  max-height: 120vh;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
}

.escritorio-cadastro-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
}

.escritorio-cadastro-titulo {
  font-size: 24px;
  font-weight: 700;
  color: #101828;
}

.escritorio-cadastro-subtitulo {
  margin-top: 4px;
  font-size: 14px;
  color: #667085;
}

.escritorio-cadastro-content {
  padding: 20px 24px 8px;
}

.escritorio-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.escritorio-form-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.escritorio-form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.escritorio-form-label {
  font-size: 12px;
  font-weight: 700;
  color: #344054;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.campo-obrigatorio {
  color: #d92d20;
}

.escritorio-input :deep(.q-field__control) {
  min-height: 40px;
  border-radius: 9px;
}

.escritorio-cadastro-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px 20px;
}
</style>
