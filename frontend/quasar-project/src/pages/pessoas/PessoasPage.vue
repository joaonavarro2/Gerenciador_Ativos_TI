<template>
  <q-page class="pessoas-page">
    <PessoasHeader v-model:filtros="filtros" :escritorios="escritorios" :departamentos="departamentos" @nova-pessoa="abrirNovaPessoa" />
    <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative q-mb-md">
      {{ erro }}
      <template #action><q-btn flat color="negative" label="Tentar novamente" @click="carregarDados" /></template>
    </q-banner>
    <q-card flat bordered class="pessoas-lista-card">
      <q-inner-loading :showing="carregando"><q-spinner size="40px" color="primary" /></q-inner-loading>
      <PessoasTable :pessoas="pessoasFiltradas" @visualizar="abrirVisualizacaoPessoa" @editar="abrirEdicaoPessoa" @excluir="abrirExclusaoPessoa" @nova-pessoa="abrirNovaPessoa" />
    </q-card>

    <NovaPessoa v-model="dialogNovaPessoa" :escritorios="escritorios" :departamentos="departamentos" :salvando="salvando" :erro="erroFormulario" @salvar="adicionarPessoa" />
    <VisualizarPessoa v-model="dialogVisualizarPessoa" :pessoa="pessoaSelecionada" />
    <EditarPessoa v-model="dialogEditarPessoa" :pessoa="pessoaSelecionada" :escritorios="escritorios" :departamentos="departamentos" :salvando="salvando" :erro="erroFormulario" @salvar="atualizarPessoa" />
    <ExcluirPessoa v-model="dialogExcluirPessoa" :pessoa="pessoaSelecionada" :loading="salvando" :erro="erroFormulario" @excluir="removerPessoa" />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '@/boot/axios.js'
import PessoasHeader from '@/components/pessoas/PessoasHeader.vue'
import PessoasTable from '@/components/pessoas/PessoasTable.vue'
import NovaPessoa from '@/components/pessoas/cards/NovaPessoa.vue'
import VisualizarPessoa from '@/components/pessoas/cards/VisualizarPessoa.vue'
import EditarPessoa from '@/components/pessoas/cards/EditarPessoa.vue'
import ExcluirPessoa from '@/components/pessoas/cards/ExcluirPessoa.vue'

const pessoas = ref([])
const escritorios = ref([])
const departamentos = ref([])
const filtros = ref({ busca: '', escritorio: null, departamento: null, status: null })
const carregando = ref(false)
const salvando = ref(false)
const erro = ref('')
const erroFormulario = ref('')
const dialogNovaPessoa = ref(false)
const dialogVisualizarPessoa = ref(false)
const dialogEditarPessoa = ref(false)
const dialogExcluirPessoa = ref(false)
const pessoaSelecionada = ref({})

const normalizar = (valor) => String(valor ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').trim().toLocaleLowerCase('pt-BR')
const pessoasFiltradas = computed(() => pessoas.value.filter((pessoa) => {
  const busca = normalizar(filtros.value.busca)
  const texto = normalizar([pessoa.nome, pessoa.matricula, pessoa.cpf, pessoa.email, pessoa.cargo, pessoa.escritorio, pessoa.departamento].join(' '))
  return (!busca || texto.includes(busca))
    && (!filtros.value.escritorio || pessoa.escritorio === filtros.value.escritorio)
    && (!filtros.value.departamento || pessoa.departamento === filtros.value.departamento)
    && (!filtros.value.status || normalizar(pessoa.status) === normalizar(filtros.value.status))
}))

function mapearPessoa(item) {
  const status = normalizar(item.status) === 'inativo' ? 'Inativo' : 'Ativo'
  return {
    ...item,
    nome: item.nomeCompleto || item.nome || '',
    escritorio: item.escritorio || '',
    departamento: item.departamento || '',
    status,
    bens: Number(item.bens || 0),
  }
}

async function carregarDados() {
  carregando.value = true
  erro.value = ''
  try {
    const [resPessoas, resEscritorios, resDepartamentos] = await Promise.all([
      api.get('/pessoas'), api.get('/escritorios'), api.get('/departamentos'),
    ])
    pessoas.value = resPessoas.data.map(mapearPessoa)
    escritorios.value = resEscritorios.data.filter((item) => normalizar(item.status) !== 'inativo')
    departamentos.value = resDepartamentos.data.filter((item) => normalizar(item.status) !== 'inativo')
  } catch (error) { erro.value = mensagemErro(error, 'carregar pessoas, escritórios e departamentos') }
  finally { carregando.value = false }
}

function mensagemErro(error, acao) {
  if (error.response?.data?.mensagem) return error.response.data.mensagem
  if (error.response?.status === 401) return 'Sua sessão expirou. Entre novamente para continuar.'
  if (error.response?.status === 403) return 'Sua conta não tem permissão para acessar Pessoas.'
  if (error.response) return `Não foi possível ${acao} (HTTP ${error.response.status}).`
  return `Não foi possível conectar ao servidor para ${acao}. Verifique se o backend está ativo.`
}

function abrirNovaPessoa() { erroFormulario.value = ''; dialogNovaPessoa.value = true }
function abrirVisualizacaoPessoa(pessoa) { pessoaSelecionada.value = pessoa; dialogVisualizarPessoa.value = true }
function abrirEdicaoPessoa(pessoa) { erroFormulario.value = ''; pessoaSelecionada.value = pessoa; dialogEditarPessoa.value = true }
function abrirExclusaoPessoa(pessoa) { erroFormulario.value = ''; pessoaSelecionada.value = pessoa; dialogExcluirPessoa.value = true }

function payload(pessoa, incluirSenha = true) {
  return {
    nomeCompleto: pessoa.nome?.trim(), matricula: pessoa.matricula?.trim() || null,
    cpf: pessoa.cpf?.trim(), email: pessoa.email?.trim(), telefone: pessoa.telefone?.trim() || null,
    cargo: pessoa.cargo?.trim(), escritorioId: pessoa.escritorioId || null,
    departamentoId: pessoa.departamentoId || null, status: pessoa.status || 'Ativo',
    ...(incluirSenha ? { senha: pessoa.senha || null, confirmarSenha: pessoa.confirmarSenha || null } : {}),
  }
}

async function adicionarPessoa(dados) {
  if (salvando.value) return
  salvando.value = true; erroFormulario.value = ''
  try {
    await api.post('/pessoas', payload(dados))
    dialogNovaPessoa.value = false
    await carregarDados()
  } catch (error) { erroFormulario.value = mensagemErro(error, 'cadastrar a pessoa') }
  finally { salvando.value = false }
}

async function atualizarPessoa(dados) {
  if (salvando.value) return
  salvando.value = true; erroFormulario.value = ''
  try {
    await api.put(`/pessoas/${dados.id}`, payload(dados))
    dialogEditarPessoa.value = false
    await carregarDados()
  } catch (error) { erroFormulario.value = mensagemErro(error, 'atualizar a pessoa') }
  finally { salvando.value = false }
}

async function removerPessoa(id) {
  if (salvando.value) return
  salvando.value = true; erroFormulario.value = ''
  try {
    await api.delete(`/pessoas/${id}`)
    dialogExcluirPessoa.value = false
    await carregarDados()
  } catch (error) { erroFormulario.value = mensagemErro(error, 'excluir a pessoa') }
  finally { salvando.value = false }
}

onMounted(carregarDados)
</script>

<style scoped>
.pessoas-lista-card { position: relative; min-height: 200px; margin-top: 24px; }
</style>
