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
      <q-banner v-if="erro || erroLocal" dense rounded class="bg-red-1 text-negative q-mb-md">{{ erro || erroLocal }}</q-banner>
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
          <q-select v-model="form.escritorioId" outlined dense :options="opcoesEscritorios" option-label="label" option-value="value" emit-value map-options class="pessoa-input" />
        </div>

        <div class="pessoa-form-group">
          <label class="pessoa-label">Departamento</label>
          <q-select v-model="form.departamentoId" outlined dense :options="opcoesDepartamentos" option-label="label" option-value="value" emit-value map-options :disable="!form.escritorioId" class="pessoa-input" />
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

      <q-banner v-if="erroBens" dense rounded class="bg-red-1 text-negative q-mb-md">
        {{ erroBens }}
        <template #action><q-btn flat color="negative" label="Tentar novamente" @click="carregarBens" /></template>
      </q-banner>
      <div v-if="carregandoBens" class="pessoa-bens-loading">
        <q-spinner size="28px" color="primary" />
        <span>Carregando bens vinculados...</span>
      </div>
      <q-list v-else-if="bensPessoa.length" bordered separator class="pessoa-bens-lista">
        <q-item v-for="bem in bensPessoa" :key="bem.id">
          <q-item-section avatar><q-avatar color="blue-1" text-color="primary" icon="inventory_2" /></q-item-section>
          <q-item-section>
            <q-item-label class="text-weight-medium">{{ bem.nome || 'Bem sem nome' }}</q-item-label>
            <q-item-label caption>
              {{ bem.patrimonio || bem.codigo || `Bem #${bem.id}` }}
              <span v-if="bem.categoria"> · {{ bem.categoria }}</span>
              <span v-if="bem.fabricante || bem.modelo"> · {{ [bem.fabricante, bem.modelo].filter(Boolean).join(' / ') }}</span>
            </q-item-label>
          </q-item-section>
          <q-item-section side><q-chip dense :color="bem.status?.toLowerCase() === 'ativo' ? 'green-1' : 'grey-3'" :text-color="bem.status?.toLowerCase() === 'ativo' ? 'green-9' : 'grey-8'">{{ bem.status || 'Sem status' }}</q-chip></q-item-section>
        </q-item>
      </q-list>
      <div v-else class="pessoa-bens-box">
        <q-icon name="inventory_2" size="28px" color="grey-6" />
        <div class="pessoa-bens-placeholder">Nenhum bem ativo vinculado a esta pessoa.</div>
      </div>

      <div class="pessoa-footer-actions">
        <q-btn flat color="grey-8" label="Cancelar" no-caps @click="fechar" />
        <q-btn color="positive" label="Salvar Alterações" no-caps type="submit" :loading="salvando" :disable="salvando" />
      </div>
    </q-form>
  </BaseDialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import { api } from '@/boot/axios.js'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  pessoa: {
    type: Object,
    default: () => ({}),
  },
  escritorios: { type: Array, default: () => [] },
  departamentos: { type: Array, default: () => [] },
  salvando: { type: Boolean, default: false },
  erro: { type: String, default: '' },
})

const emit = defineEmits(['update:modelValue', 'salvar'])

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const statusOptions = ['Ativo', 'Inativo']
const erroLocal = ref('')
const bensPessoa = ref([])
const carregandoBens = ref(false)
const erroBens = ref('')
const opcoesEscritorios = computed(() => props.escritorios.map((item) => ({ label: item.nome, value: item.id })))
const opcoesDepartamentos = computed(() => props.departamentos
  .filter((item) => !item.escritorioId || Number(item.escritorioId) === Number(form.value.escritorioId))
  .map((item) => ({ label: item.nome, value: item.id })))

const form = ref({
  nome: '',
  matricula: '',
  cpf: '',
  email: '',
  telefone: '',
  cargo: '',
  status: 'Ativo',
  escritorioId: null,
  departamentoId: null,
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
      escritorioId: value.escritorioId || null,
      departamentoId: value.departamentoId || null,
      senha: '',
      confirmarSenha: '',
    }
  },
  { immediate: true }
)

watch(() => form.value.escritorioId, (id, anterior) => {
  if (id !== anterior) form.value.departamentoId = null
})

let requisicaoBens = 0
async function carregarBens() {
  const id = props.pessoa?.id
  if (!props.modelValue || !id) {
    requisicaoBens++
    bensPessoa.value = []
    carregandoBens.value = false
    erroBens.value = ''
    return
  }
  const atual = ++requisicaoBens
  carregandoBens.value = true
  erroBens.value = ''
  try {
    const { data } = await api.get(`/pessoas/${id}/bens`)
    if (atual === requisicaoBens) bensPessoa.value = Array.isArray(data) ? data : []
  } catch (error) {
    if (atual === requisicaoBens) erroBens.value = error.response?.data?.mensagem || 'Não foi possível carregar os bens vinculados.'
  } finally {
    if (atual === requisicaoBens) carregandoBens.value = false
  }
}

watch(() => [props.modelValue, props.pessoa?.id], carregarBens, { immediate: true })

function fechar() {
  emit('update:modelValue', false)
}

function salvar() {
  erroLocal.value = ''
  if (props.salvando) return
  if (props.salvando || !form.value.nome?.trim() || !form.value.cpf || !form.value.email || !form.value.cargo?.trim() || !form.value.escritorioId || !form.value.departamentoId) return
  if (form.value.senha && form.value.senha.length < 8) {
    erroLocal.value = 'A senha deve ter pelo menos 8 caracteres.'
    return
  }
  if (form.value.senha && form.value.senha !== form.value.confirmarSenha) {
    erroLocal.value = 'A senha e a confirmação não conferem.'
    return
  }
  emit('salvar', {
    ...props.pessoa,
    ...form.value,
  })
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

.pessoa-bens-loading { display: flex; align-items: center; justify-content: center; gap: 12px; min-height: 88px; color: #667085; }
.pessoa-bens-lista { max-height: 260px; overflow-y: auto; border-radius: 12px; }

.pessoa-footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>
