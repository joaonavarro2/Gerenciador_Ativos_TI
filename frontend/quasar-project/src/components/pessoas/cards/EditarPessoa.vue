<template>
  <BaseDialog
    :model-value="dialog"
    title="Editar Pessoa"
    subtitle="Atualize as informações do colaborador."
    icon="edit"
    icon-color="primary"
    icon-background="amber-1"
    width="760px"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-form class="q-pa-lg" @submit.prevent="salvar">
      <div class="pessoa-form-grid">
        <div class="pessoa-form-group">
          <label class="pessoa-label">Nome completo</label>
          <q-input v-model="form.nome" outlined dense class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">CPF</label>
          <q-input v-model="form.cpf" outlined dense class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">E-mail</label>
          <q-input v-model="form.email" outlined dense class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Telefone</label>
          <q-input v-model="form.telefone" outlined dense class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Cargo</label>
          <q-input v-model="form.cargo" outlined dense class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Status</label>
          <q-select v-model="form.status" outlined dense :options="statusOptions" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Escritório</label>
          <q-select v-model="form.escritorio" outlined dense :options="opcoesEscritorios" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Departamento</label>
          <q-select v-model="form.departamento" outlined dense :options="opcoesDepartamentos" class="pessoa-input" />
        </div>
      </div>

      <div class="pessoa-section-title">SENHA DE ACESSO</div>

      <div class="pessoa-form-grid">
        <div class="pessoa-form-group">
          <label class="pessoa-label">Senha</label>
          <q-input v-model="form.senha" outlined dense type="password" placeholder="Digite a nova senha" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Confirmar Senha</label>
          <q-input v-model="form.confirmarSenha" outlined dense type="password" placeholder="Confirme a nova senha" class="pessoa-input" />
        </div>
      </div>

      <div class="pessoa-section-title">BENS ATRIBUÍDOS</div>

      <div class="pessoa-bens-box">
        <q-icon name="inventory_2" size="28px" color="grey-6" />
        <div class="pessoa-bens-placeholder">Nenhum bem atribuído</div>
      </div>

      <div class="pessoa-footer-actions">
        <q-btn flat color="grey-8" label="Cancelar" no-caps @click="fechar" />
        <q-btn color="positive" label="Salvar Alterações" no-caps type="submit" />
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
  pessoa: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:modelValue', 'salvar'])

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const statusOptions = ['Ativo', 'Inativo']
const opcoesEscritorios = [
  'Escritório Regional Salvador',
  'Escritório de Feira de Santana',
  'Escritório de Vitória da Conquista',
  'Escritório de Barreiras',
]
const opcoesDepartamentos = [
  'Assessoria da Diretoria',
  'Controle Interno',
  'Diretoria Geral',
  'Recursos Humanos',
  'Financeiro',
]

const form = ref({
  nome: '',
  matricula: '',
  cpf: '',
  email: '',
  telefone: '',
  cargo: '',
  status: 'Ativo',
  escritorio: '',
  departamento: '',
  senha: '',
  confirmarSenha: '',
})

watch(
  () => props.pessoa,
  (value) => {
    form.value = {
      nome: value.nome || '',
      matricula: value.matricula || '',
      cpf: value.cpf || '',
      email: value.email || '',
      telefone: value.telefone || '',
      cargo: value.cargo || '',
      status: value.status || 'Ativo',
      escritorio: value.escritorio || '',
      departamento: value.departamento || '',
      senha: '',
      confirmarSenha: '',
    }
  },
  { immediate: true }
)

function fechar() {
  emit('update:modelValue', false)
}

function salvar() {
  emit('salvar', {
    ...props.pessoa,
    ...form.value,
  })
  emit('update:modelValue', false)
}
</script>

<style scoped>
.pessoa-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px 20px;
}

.pessoa-form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pessoa-label {
  font-size: 13px;
  font-weight: 700;
  color: #344054;
}

.pessoa-input :deep(.q-field__control) {
  min-height: 44px;
  border-radius: 10px;
}

.pessoa-section-title {
  margin: 24px 0 14px;
  text-align: center;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #667085;
}

.pessoa-bens-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  min-height: 120px;
  border: 1px dashed #d0d5dd;
  border-radius: 12px;
  background: #f8fafc;
  color: #475467;
}

.pessoa-bens-placeholder {
  font-size: 14px;
  color: #667085;
}

.pessoa-footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>
