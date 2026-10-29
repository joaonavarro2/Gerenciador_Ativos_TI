<template>
  <q-page class="escritorios-page">
    <EscritoriosHeader v-model:filtros="filtros" :escritorios="escritorios" @novo-escritorio="abrirNovoEscritorio" />
    <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative q-mb-md">
      {{ erro }}
      <template #action><q-btn flat color="negative" label="Tentar novamente" @click="carregarEscritorios" /></template>
    </q-banner>
    <q-card flat bordered class="escritorios-lista-card">
      <q-inner-loading :showing="carregando"><q-spinner size="40px" color="primary" /></q-inner-loading>
      <EscritoriosTable
        :escritorios="escritoriosFiltrados"
        :possui-filtros="possuiFiltros"
        :filtros="filtros"
        @visualizar="abrirVisualizacaoEscritorio"
        @editar="abrirEdicaoEscritorio"
        @excluir="abrirExclusaoEscritorio"
      />
    </q-card>
    <NovoEscritorio v-model="dialogNovo" :salvando="salvando" :erro="erroFormulario" @salvo="adicionarEscritorio" />
    <VisualizarEscritorio v-model="dialogVisualizar" :escritorio="escritorioSelecionado" />
    <EditarEscritorio v-model="dialogEditar" :escritorio="escritorioSelecionado" :salvando="salvando" :erro="erroFormulario" @salvar="atualizarEscritorio" />
    <ExcluirEscritorio v-model="dialogExcluir" :escritorio="escritorioSelecionado" :loading="salvando" :erro="erroFormulario" @excluir="removerEscritorio" />
  </q-page>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '@/boot/axios.js'
import EscritoriosHeader from '@/components/escritorios/EscritoriosHeader.vue'
import EscritoriosTable from '@/components/escritorios/EscritoriosTable.vue'
import NovoEscritorio from '@/components/escritorios/cards/NovoEscritorio.vue'
import VisualizarEscritorio from '@/components/escritorios/cards/VisualizarEscritorio.vue'
import EditarEscritorio from '@/components/escritorios/cards/EditarEscritorio.vue'
import ExcluirEscritorio from '@/components/escritorios/cards/ExcluirEscritorio.vue'

const escritorios = ref([])
const carregando = ref(false)
const salvando = ref(false)
const erro = ref('')
const erroFormulario = ref('')
const filtros = ref({ busca: '', estado: null, cidade: null, status: null })
const dialogNovo = ref(false)
const dialogVisualizar = ref(false)
const dialogEditar = ref(false)
const dialogExcluir = ref(false)
const escritorioSelecionado = ref({})

const possuiFiltros = computed(() => Object.values(filtros.value).some(Boolean))
const normalizar = (valor) => String(valor ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').trim().toLocaleLowerCase('pt-BR')
const escritoriosFiltrados = computed(() => escritorios.value.filter((item) => {
  const busca = normalizar(filtros.value.busca)
  const correspondeBusca = !busca || normalizar(`${item.nome} ${item.cidade} ${item.estado} ${item.responsavel || item.gestor}`).includes(busca)
  const correspondeEstado = !filtros.value.estado || normalizar(item.estado) === normalizar(filtros.value.estado)
  const correspondeCidade = !filtros.value.cidade || normalizar(item.cidade) === normalizar(filtros.value.cidade)
  const correspondeStatus = !filtros.value.status || normalizar(item.status) === normalizar(filtros.value.status)
  return correspondeBusca && correspondeEstado && correspondeCidade && correspondeStatus
}))

async function carregarEscritorios() {
  carregando.value = true
  erro.value = ''
  try {
    const { data } = await api.get('/escritorios')
    escritorios.value = data.map((item) => ({ ...item, responsavel: item.responsavel || item.gestor || '', status: normalizar(item.status) }))
    escritorios.value = escritorios.value.map((item) => ({
      ...item,
      status: item.status === 'em expansao' ? 'Em expansão' : item.status === 'inativo' ? 'Inativo' : 'Ativo',
    }))
  } catch (error) {
    erro.value = mensagemErro(error, 'carregar os escritórios')
  } finally { carregando.value = false }
}

function mensagemErro(error, acao) {
  if (error.response?.data?.mensagem) return error.response.data.mensagem
  if (error.response?.status === 401) return 'Sua sessão expirou. Entre novamente para continuar.'
  if (error.response?.status === 403) return 'Sua conta não tem permissão para acessar os escritórios.'
  if (error.response) return `Não foi possível ${acao} (HTTP ${error.response.status}).`
  return `Não foi possível conectar ao servidor para ${acao}. Verifique se o backend está ativo.`
}

function abrirNovoEscritorio() { erroFormulario.value = ''; dialogNovo.value = true }
function abrirVisualizacaoEscritorio(item) { escritorioSelecionado.value = item; dialogVisualizar.value = true }
function abrirEdicaoEscritorio(item) { erroFormulario.value = ''; escritorioSelecionado.value = item; dialogEditar.value = true }
function abrirExclusaoEscritorio(item) { erroFormulario.value = ''; escritorioSelecionado.value = item; dialogExcluir.value = true }

async function adicionarEscritorio(dados) {
  salvando.value = true
  erroFormulario.value = ''
  try {
    await api.post('/escritorios', payload(dados))
    dialogNovo.value = false
    await carregarEscritorios()
  } catch (error) { erroFormulario.value = mensagemErro(error, 'cadastrar o escritório') }
  finally { salvando.value = false }
}

async function atualizarEscritorio(dados) {
  salvando.value = true
  erroFormulario.value = ''
  try {
    await api.put(`/escritorios/${dados.id}`, payload(dados))
    dialogEditar.value = false
    await carregarEscritorios()
  } catch (error) { erroFormulario.value = mensagemErro(error, 'atualizar o escritório') }
  finally { salvando.value = false }
}

async function removerEscritorio(id) {
  salvando.value = true
  erroFormulario.value = ''
  try {
    await api.delete(`/escritorios/${id}`)
    dialogExcluir.value = false
    await carregarEscritorios()
  } catch (error) { erroFormulario.value = mensagemErro(error, 'excluir o escritório') }
  finally { salvando.value = false }
}

function payload(item) {
  return {
    nome: item.nome?.trim(), cidade: item.cidade?.trim(), estado: item.estado?.trim() || null,
    cep: item.cep?.trim() || null, endereco: item.endereco?.trim() || null,
    telefone: item.telefone?.trim() || null, email: item.email?.trim() || null,
    gestor: item.gestor?.trim() || item.responsavel?.trim() || null, status: item.status || 'Ativo',
  }
}

onMounted(carregarEscritorios)
</script>

<style scoped>
.escritorios-page { padding: 24px; }
.escritorios-lista-card { margin-top: 24px; position: relative; min-height: 180px; }
</style>
