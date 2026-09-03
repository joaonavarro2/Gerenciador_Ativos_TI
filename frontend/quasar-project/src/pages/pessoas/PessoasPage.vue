<template>
  <q-page class="pessoas-page">
    <PessoasHeader
      v-model:filtros="filtros"
      @nova-pessoa="abrirNovaPessoa"
    />

    <PessoasTable
      :pessoas="pessoasFiltradas"
      @visualizar="abrirVisualizacaoPessoa"
      @editar="abrirEdicaoPessoa"
      @excluir="abrirExclusaoPessoa"
    />

    <NovaPessoa v-model="dialogNovaPessoa" @salvar="adicionarPessoa" />
    <VisualizarPessoa v-model="dialogVisualizarPessoa" :pessoa="pessoaSelecionada" />
    <EditarPessoa v-model="dialogEditarPessoa" :pessoa="pessoaSelecionada" @salvar="atualizarPessoa" />
    <ExcluirPessoa v-model="dialogExcluirPessoa" :pessoa="pessoaSelecionada" @excluir="removerPessoa" />
  </q-page>
</template>

<script setup>
import { computed, ref } from 'vue'

import PessoasHeader from '@/components/pessoas/PessoasHeader.vue'
import PessoasTable from '@/components/pessoas/PessoasTable.vue'
import NovaPessoa from '@/components/pessoas/cards/NovaPessoa.vue'
import VisualizarPessoa from '@/components/pessoas/cards/VisualizarPessoa.vue'
import EditarPessoa from '@/components/pessoas/cards/EditarPessoa.vue'
import ExcluirPessoa from '@/components/pessoas/cards/ExcluirPessoa.vue'

const filtros = ref({
  busca: '',
  escritorio: null,
  departamento: null,
  status: null
})

const pessoas = ref([
  {
    id: 1,
    nome: 'Ana Paula Souza',
    matricula: 'MAT-00241',
    cpf: '012.345.678-90',
    email: 'ana.souza@empresa.com',
    cargo: 'Gerente',
    escritorio: 'Escritório Regional Salvador',
    departamento: 'Assessoria da Diretoria',
    status: 'Ativo',
    bens: 12,
  },
  {
    id: 2,
    nome: 'Marcos Lima',
    matricula: 'MAT-00314',
    cpf: '123.456.789-00',
    email: 'marcos.lima@empresa.com',
    cargo: 'Analista',
    escritorio: 'Escritório de Feira de Santana',
    departamento: 'Financeiro',
    status: 'Ativo',
    bens: 8,
  },
  {
    id: 3,
    nome: 'Sérgio Almeida',
    matricula: 'MAT-00572',
    cpf: '321.654.987-11',
    email: 'sergio.almeida@empresa.com',
    cargo: 'Coordenador',
    escritorio: 'Escritório de Barreiras',
    departamento: 'Controle Interno',
    status: 'Inativo',
    bens: 4,
  },
])

const dialogNovaPessoa = ref(false)
const dialogVisualizarPessoa = ref(false)
const dialogEditarPessoa = ref(false)
const dialogExcluirPessoa = ref(false)
const pessoaSelecionada = ref({})

const pessoasFiltradas = computed(() => {
  const busca = filtros.value.busca.trim().toLowerCase()

  return pessoas.value.filter((pessoa) => {
    if (busca) {
      const texto = [
        pessoa.nome,
        pessoa.matricula,
        pessoa.cpf,
        pessoa.email,
        pessoa.cargo,
        pessoa.escritorio,
        pessoa.departamento
      ]
        .join(' ')
        .toLowerCase()

      if (!texto.includes(busca)) {
        return false
      }
    }

    if (filtros.value.escritorio && pessoa.escritorio !== filtros.value.escritorio) {
      return false
    }

    if (filtros.value.departamento && pessoa.departamento !== filtros.value.departamento) {
      return false
    }

    if (filtros.value.status && pessoa.status !== filtros.value.status) {
      return false
    }

    return true
  })
})

function abrirNovaPessoa() {
  dialogNovaPessoa.value = true
}

function abrirVisualizacaoPessoa(pessoa) {
  pessoaSelecionada.value = pessoa
  dialogVisualizarPessoa.value = true
}

function abrirEdicaoPessoa(pessoa) {
  pessoaSelecionada.value = pessoa
  dialogEditarPessoa.value = true
}

function abrirExclusaoPessoa(pessoa) {
  pessoaSelecionada.value = pessoa
  dialogExcluirPessoa.value = true
}

function adicionarPessoa(novaPessoa) {
  pessoas.value.unshift({
    ...novaPessoa,
    id: Date.now(),
    nome: novaPessoa.nome || 'Colaborador',
    matricula: novaPessoa.matricula || `MAT-${String(Date.now()).slice(-6)}`,
    cpf: novaPessoa.cpf || '000.000.000-00',
    email: novaPessoa.email || 'colaborador@empresa.com',
    cargo: novaPessoa.cargo || novaPessoa.perfil || 'Operador',
    bens: novaPessoa.bens || 0,
    status: novaPessoa.status || 'Ativo',
  })
}

function atualizarPessoa(pessoaAtualizada) {
  pessoas.value = pessoas.value.map((item) =>
    item.id === pessoaAtualizada.id ? { ...item, ...pessoaAtualizada } : item
  )
}

function removerPessoa(id) {
  pessoas.value = pessoas.value.filter((item) => item.id !== id)
}
</script>
