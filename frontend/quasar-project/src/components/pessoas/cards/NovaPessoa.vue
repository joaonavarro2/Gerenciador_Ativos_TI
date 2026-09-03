<template>
  <BaseDialog
    :model-value="dialog"
    title="Cadastrar Nova Pessoa"
    subtitle="Preencha os dados do colaborador para criar o registro."
    icon="person_add"
    icon-color="primary"
    icon-background="teal-1"
    width="820px"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-form class="q-pa-lg" @submit.prevent="salvar">
      <div class="pessoa-form-grid">
        <div class="pessoa-form-group">
          <label class="pessoa-label">Nome completo <span class="required">*</span></label>
          <q-input v-model="form.nome" outlined dense placeholder="Ex: João da Silva" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">CPF <span class="required">*</span></label>
          <q-input v-model="form.cpf" outlined dense placeholder="000.000.000-00" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">E-mail <span class="required">*</span></label>
          <q-input v-model="form.email" outlined dense placeholder="nome@empresa.com" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Telefone</label>
          <q-input v-model="form.telefone" outlined dense placeholder="(71) 99999-9999" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Cargo <span class="required">*</span></label>
          <q-input v-model="form.cargo" outlined dense placeholder="Ex: Analista de TI" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Status</label>
          <q-select v-model="form.status" outlined dense :options="statusOptions" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Escritório <span class="required">*</span></label>
          <q-select
            v-model="form.escritorio"
            outlined
            dense
            :options="opcoesEscritorios"
            label="Selecione..."
            class="pessoa-input"
          />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Departamento <span class="required">*</span></label>
          <q-select
            v-model="form.departamento"
            outlined
            dense
            :options="opcoesDepartamentos"
            label="Selecione..."
            class="pessoa-input"
          />
        </div>
      </div>

      <div class="pessoa-section-title">SENHA DE ACESSO</div>

      <div class="pessoa-form-grid">
        <div class="pessoa-form-group">
          <label class="pessoa-label">Senha <span class="required">*</span></label>
          <q-input v-model="form.senha" outlined dense type="password" placeholder="Digite a senha" class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Confirmar Senha <span class="required">*</span></label>
          <q-input v-model="form.confirmarSenha" outlined dense type="password" placeholder="Confirme a senha" class="pessoa-input" />
        </div>
      </div>

      <div class="pessoa-section-title">BENS ATRIBUÍDOS</div>

      <div class="pessoa-bens-box">
        <q-icon name="inventory_2" size="28px" color="grey-6" />
        <div class="pessoa-bens-placeholder">Nenhum bem atribuído</div>
      </div>

      <div class="pessoa-footer-actions">
        <q-btn flat color="grey-8" label="Cancelar" no-caps @click="fechar" />
        <q-btn color="positive" label="Salvar Registro" no-caps type="submit" />
      </div>
    </q-form>
  </BaseDialog>
</template>

<script setup>
import { computed, ref } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:modelValue', 'salvar'])

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const form = ref({
  nome: '',
  cpf: '',
  email: '',
  telefone: '',
  cargo: '',
  escritorio: null,
  departamento: null,
  status: 'Ativo',
  senha: '',
  confirmarSenha: '',
})

const opcoesEscritorios = [
  'Escritório Regional Salvador',
  'Escritório de Feira de Santana',
  'Escritório de Vitória da Conquista',
  'Escritório de Barreiras',
  'Escritório de Alagoinhas',
]

const opcoesDepartamentos = [
  'Assessoria da Diretoria',
  'Controle Interno',
  'Diretoria Geral',
  'Recursos Humanos',
  'Financeiro',
]

const statusOptions = ['Ativo', 'Inativo']

function fechar() {
  emit('update:modelValue', false)
}

function salvar() {
  if (!form.value.nome || !form.value.cpf || !form.value.email || !form.value.escritorio || !form.value.departamento) return

  emit('salvar', {
    ...form.value,
    id: Date.now(),
    matricula: `MAT-${String(Date.now()).slice(-6)}`,
    bens: 0,
  })

  form.value = {
    nome: '',
    cpf: '',
    email: '',
    telefone: '',
    cargo: '',
    escritorio: null,
    departamento: null,
    status: 'Ativo',
    senha: '',
    confirmarSenha: '',
  }

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

.required {
  color: #d92d20;
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
  min-height: 140px;
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
