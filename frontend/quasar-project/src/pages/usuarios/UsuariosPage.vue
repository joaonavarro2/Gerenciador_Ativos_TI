<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-md">
      <div>
        <div class="text-h4 text-weight-bold">Usuários</div>
        <div class="text-subtitle2 text-grey-7">Contas com acesso ao STECAR</div>
      </div>
      <q-btn v-if="administradorGeral" color="positive" icon="person_add" label="Novo usuário" no-caps @click="abrirNovo" />
    </div>

    <q-banner v-if="erro" rounded class="bg-red-1 text-negative q-mb-md">
      {{ erro }}
      <template #action><q-btn flat label="Tentar novamente" @click="carregar" /></template>
    </q-banner>

    <q-card flat bordered>
      <q-card-section class="row q-col-gutter-md">
        <div class="col-12 col-md-6"><q-input v-model="busca" outlined dense clearable label="Buscar por nome ou e-mail" /></div>
        <div class="col-6 col-md-3"><q-select v-model="filtroFuncao" outlined dense clearable label="Função" :options="funcoesFiltro" /></div>
        <div class="col-6 col-md-3"><q-select v-model="filtroStatus" outlined dense clearable label="Status" :options="statusOptions" /></div>
      </q-card-section>
      <q-table :rows="filtrados" :columns="colunas" row-key="id" :loading="carregando" no-data-label="Nenhum usuário encontrado">
        <template #body-cell-funcao="props"><q-td :props="props">{{ rotuloFuncao(props.value) }}</q-td></template>
        <template #body-cell-status="props"><q-td :props="props"><q-badge :color="props.value === 'ATIVO' ? 'positive' : 'grey'">{{ props.value }}</q-badge></q-td></template>
        <template #body-cell-acoes="props">
          <q-td :props="props" v-if="administradorGeral">
            <q-btn flat round dense icon="edit" aria-label="Editar usuário" @click="abrirEdicao(props.row)" />
            <q-btn flat round dense :icon="props.row.status === 'ATIVO' ? 'person_off' : 'person'" :disable="ehProprio(props.row) || (props.row.funcao === 'ADMINISTRADOR GERAL' && props.row.status === 'ATIVO' && quantidadeAgAtivos <= 1)" :aria-label="props.row.status === 'ATIVO' ? 'Desativar usuário' : 'Ativar usuário'" @click="alterarStatus(props.row)" />
            <q-btn flat round dense color="negative" icon="delete" aria-label="Excluir usuário" :disable="ehProprio(props.row) || (props.row.funcao === 'ADMINISTRADOR GERAL' && props.row.status === 'ATIVO' && quantidadeAgAtivos <= 1)" @click="confirmarExclusao(props.row)" />
          </q-td>
          <q-td v-else :props="props">Consulta</q-td>
        </template>
      </q-table>
    </q-card>

    <q-dialog v-model="dialogAberto" persistent>
      <q-card style="width: 560px; max-width: 95vw">
        <q-card-section class="row items-center justify-between"><div class="text-h6">{{ editando ? 'Editar usuário' : 'Novo usuário' }}</div><q-btn flat round dense icon="close" v-close-popup /></q-card-section>
        <q-card-section class="q-gutter-md">
          <q-input v-model="form.nomeCompleto" outlined label="Nome completo" maxlength="150" />
          <q-input v-model="form.email" outlined type="email" label="E-mail" maxlength="150" />
          <q-select v-model="form.funcao" outlined label="Função" :options="funcoes" :disable="editando && ehProprio(form)" />
          <template v-if="!editando">
            <q-input v-model="form.senha" outlined type="password" label="Senha inicial" autocomplete="new-password" />
            <q-input v-model="form.confirmarSenha" outlined type="password" label="Confirmar senha" autocomplete="new-password" />
            <q-select v-model="form.status" outlined label="Status inicial" :options="statusOptions" />
          </template>
          <div v-if="erroFormulario" class="text-negative">{{ erroFormulario }}</div>
        </q-card-section>
        <q-card-actions align="right"><q-btn flat label="Cancelar" v-close-popup /><q-btn color="positive" :loading="salvando" :label="editando ? 'Salvar' : 'Cadastrar'" @click="salvar" /></q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useQuasar } from 'quasar'
import { api } from '@/boot/axios.js'
import { ehAdministradorGeral, usuarioAtual } from '@/services/auth.js'

const $q = useQuasar()
const administradorGeral = ehAdministradorGeral()
const sessao = usuarioAtual()
const usuarios = ref([])
const carregando = ref(false)
const salvando = ref(false)
const erro = ref('')
const erroFormulario = ref('')
const busca = ref('')
const filtroFuncao = ref(null)
const filtroStatus = ref(null)
const dialogAberto = ref(false)
const editando = ref(false)
const form = reactive({ id: null, nomeCompleto: '', email: '', senha: '', confirmarSenha: '', funcao: 'ADMINISTRADOR', status: 'ATIVO' })
const funcoes = ['ADMINISTRADOR', 'ADMINISTRADOR GERAL']
const funcoesFiltro = [...funcoes]
const statusOptions = ['ATIVO', 'INATIVO']
const colunas = [
  { name: 'nomeCompleto', label: 'Nome', field: 'nomeCompleto', align: 'left', sortable: true },
  { name: 'email', label: 'E-mail', field: 'email', align: 'left', sortable: true },
  { name: 'funcao', label: 'Função', field: 'funcao', align: 'left', sortable: true },
  { name: 'status', label: 'Status', field: 'status', align: 'left', sortable: true },
  { name: 'acoes', label: 'Ações', field: 'id', align: 'right' },
]
const filtrados = computed(() => usuarios.value.filter((u) => {
  const texto = `${u.nomeCompleto} ${u.email}`.toLocaleLowerCase('pt-BR')
  return (!busca.value || texto.includes(busca.value.toLocaleLowerCase('pt-BR')))
    && (!filtroFuncao.value || u.funcao === filtroFuncao.value)
    && (!filtroStatus.value || u.status === filtroStatus.value)
}))
const quantidadeAgAtivos = computed(() => usuarios.value.filter((u) => u.funcao === 'ADMINISTRADOR GERAL' && u.status === 'ATIVO').length)

function ehProprio(usuario) { return String(usuario?.id) === String(sessao?.id) }
function rotuloFuncao(funcao) { return funcao === 'ADMINISTRADOR GERAL' || funcao === 'ADMINISTRADOR_GERAL' ? 'ADMINISTRADOR GERAL' : funcao }

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const { data } = await api.get('/usuarios')
    usuarios.value = Array.isArray(data) ? data : []
  } catch (e) {
    erro.value = e.response?.data?.mensagem || 'Não foi possível carregar os usuários.'
  } finally { carregando.value = false }
}

function resetar() {
  Object.assign(form, { id: null, nomeCompleto: '', email: '', senha: '', confirmarSenha: '', funcao: 'ADMINISTRADOR', status: 'ATIVO' })
  erroFormulario.value = ''
}
function abrirNovo() { resetar(); editando.value = false; dialogAberto.value = true }
function abrirEdicao(usuario) {
  resetar()
  Object.assign(form, { id: usuario.id, nomeCompleto: usuario.nomeCompleto, email: usuario.email, funcao: usuario.funcao, status: usuario.status })
  editando.value = true
  dialogAberto.value = true
}

async function salvar() {
  erroFormulario.value = ''
  if (!form.nomeCompleto.trim() || !form.email.trim()) { erroFormulario.value = 'Informe nome e e-mail.'; return }
  if (!editando.value && (form.senha.length < 8 || form.senha.length > 100)) { erroFormulario.value = 'A senha deve ter entre 8 e 100 caracteres.'; return }
  if (!editando.value && form.senha !== form.confirmarSenha) { erroFormulario.value = 'A confirmação da senha não confere.'; return }
  salvando.value = true
  try {
    if (editando.value) {
      await api.put(`/usuarios/${form.id}`, { nomeCompleto: form.nomeCompleto, email: form.email, funcao: form.funcao })
    } else {
      await api.post('/usuarios', { nomeCompleto: form.nomeCompleto, email: form.email, senha: form.senha, funcao: form.funcao, status: form.status })
    }
    dialogAberto.value = false
    await carregar()
    $q.notify({ type: 'positive', message: editando.value ? 'Usuário atualizado.' : 'Usuário cadastrado.' })
  } catch (e) { erroFormulario.value = e.response?.data?.mensagem || 'Não foi possível salvar o usuário.' }
  finally { salvando.value = false }
}

async function alterarStatus(usuario) {
  const novo = usuario.status === 'ATIVO' ? 'INATIVO' : 'ATIVO'
  try { await api.patch(`/usuarios/${usuario.id}/status?status=${novo}`); await carregar() }
  catch (e) { $q.notify({ type: 'negative', message: e.response?.data?.mensagem || 'Não foi possível alterar o status.' }) }
}
function confirmarExclusao(usuario) {
  $q.dialog({ title: 'Excluir usuário', message: `Excluir o acesso de ${usuario.nomeCompleto}?`, cancel: true, persistent: true })
    .onOk(async () => {
      try { await api.delete(`/usuarios/${usuario.id}`); await carregar(); $q.notify({ type: 'positive', message: 'Usuário excluído.' }) }
      catch (e) { $q.notify({ type: 'negative', message: e.response?.data?.mensagem || 'Não foi possível excluir o usuário.' }) }
    })
}

onMounted(carregar)
</script>
