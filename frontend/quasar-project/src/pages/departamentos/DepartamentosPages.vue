<template>

  <q-page class="departamentos-page">

    <!-- =====================================================
         HEADER
    ====================================================== -->

    <DepartamentosHeader
      v-model:filtros="filtros"
      :total-departamentos="totalDepartamentos"
      :total-bens="totalBens"
      :gestores-ativos="gestoresAtivos"
      @novo-departamento="abrirNovoDepartamento"
    />

    <NovoDepartamento
      v-model="mostrarNovoDepartamento"
      @salvar="salvarNovoDepartamento"
    />

    <!-- =====================================================
         LISTA DE DEPARTAMENTOS
    ====================================================== -->

    <DepartamentosTable
      v-if="departamentoSelecionado === null"
      :departamentos="departamentosFiltrados"
      @visualizar-departamento="abrirVisualizacaoDepartamento"
    />


    <!-- =====================================================
         VISUALIZAÇÃO DO DEPARTAMENTO
    ====================================================== -->

    <VisualizacaoDepartamento
      v-else
      :departamento="departamentoSelecionado"
      @voltar="voltarDepartamentos"
    />

  </q-page>

</template>
<script setup>

import { ref, computed } from 'vue'

import DepartamentosHeader
  from '@/components/departamentos/DepartamentosHeader.vue'

import DepartamentosTable
  from '@/components/departamentos/DepartamentosTable.vue'

import VisualizacaoDepartamento
  from '@/components/departamentos/cards/VisualizacaoDepartamento.vue'

import NovoDepartamento
  from '@/components/departamentos/cards/NovoDepartamento.vue'



/* ==========================================================
   MOCKS DOS DEPARTAMENTOS
========================================================== */

/*
 * MOCKS TEMPORÁRIOS PARA O FRONTEND.
 *
 * FUTURO BACKEND:
 * Estes dados serão substituídos pelos dados
 * retornados pela API do Spring Boot.
 *
 * Exemplo:
 *
 * const response = await api.get('/departamentos')
 * departamentos.value = response.data
 */

const departamentos = ref([

  {
    id: 1,

    sigla: 'AD',

    nome: 'Assessoria da Diretoria',

    descricao:
      'Assessoramento direto à diretoria executiva.',

    bens: 42,

    consertos: 3,

    status: 'Ativo',

    cor: '#2563EB',

    corFundo: '#DBEAFE'

  },


  {
    id: 2,

    sigla: 'CI',

    nome: 'Controle Interno',

    descricao:
      'Auditoria e controle de processos internos.',

    bens: 58,

    consertos: 5,

    status: 'Ativo',

    cor: '#DB2777',

    corFundo: '#FCE7F3'

  },


  {
    id: 3,

    sigla: 'DG',

    nome: 'Diretoria Geral',

    descricao:
      'Órgão máximo de gestão e decisão estratégica.',

    bens: 89,

    consertos: 2,

    status: 'Ativo',

    cor: '#059669',

    corFundo: '#D1FAE5'

  },


  {
    id: 4,

    sigla: 'COM',

    nome: 'Assessoria de Comunicação',

    descricao:
      'Comunicação institucional, imprensa e mídia digital.',

    bens: 34,

    consertos: 4,

    status: 'Ativo',

    cor: '#D97706',

    corFundo: '#FEF3C7'

  },


  {
    id: 5,

    sigla: 'JUR',

    nome: 'Assessoria Jurídica',

    descricao:
      'Assessoria e acompanhamento jurídico institucional.',

    bens: 27,

    consertos: 2,

    status: 'Ativo',

    cor: '#7C3AED',

    corFundo: '#EDE9FE'

  },


  {
    id: 6,

    sigla: 'RH',

    nome: 'Recursos Humanos',

    descricao:
      'Gestão de pessoas e atividades relacionadas aos colaboradores.',

    bens: 31,

    consertos: 6,

    status: 'Ativo',

    cor: '#DC2626',

    corFundo: '#FEE2E2'

  }

])



/* ==========================================================
   FILTROS
========================================================== */

/*
 * Os filtros pertencem à Page.
 *
 * O DepartamentosHeader apenas altera esses valores.
 */

const filtros = ref({

  busca: '',

  departamento: null,

  cidade: null,

  status: null

})



/* ==========================================================
   RESUMO
========================================================== */


/*
 * Total de departamentos.
 */

const totalDepartamentos = computed(() => {

  return departamentos.value.length

})



/*
 * Total de bens associados
 * aos departamentos.
 */

const totalBens = computed(() => {

  return departamentos.value.reduce(

    (total, departamento) => {

      return total + departamento.bens

    },

    0

  )

})



/*
 * Total de consertos associados
 * aos departamentos.
 *
 * FUTURO BACKEND:
 * Essa informação poderá vir diretamente
 * da API ou ser calculada a partir dos
 * consertos relacionados aos departamentos.
 */

// const totalConsertos = computed(() => {

//   return departamentos.value.reduce(

//     (total, departamento) => {

//       return total + departamento.consertos

//     },

//     0

//   )

// })



/* ==========================================================
   DEPARTAMENTOS FILTRADOS
========================================================== */

/*
 * Aplica os filtros enviados pelo Header.
 */

const departamentosFiltrados = computed(() => {

  const filtro = filtros.value

  return departamentos.value.filter(departamento => {


    /* =====================================================
       BUSCA
    ====================================================== */

    if (filtro.busca) {

      const busca =
        filtro.busca
          .toLowerCase()
          .trim()


      const textoDepartamento = `

        ${departamento.nome}

        ${departamento.sigla}

        ${departamento.descricao}

      `.toLowerCase()


      if (
        !textoDepartamento.includes(busca)
      ) {

        return false

      }

    }


    /* =====================================================
       STATUS
    ====================================================== */

    if (filtro.status) {

      if (
        departamento.status !==
        filtro.status
      ) {

        return false

      }

    }


    /*
     * Estes filtros continuam preparados
     * para uma futura implementação.
     *
     * FUTURO BACKEND:
     * poderão ser relacionados aos dados
     * de escritório/departamento.
     */

    if (filtro.departamento) {

      // FUTURO BACKEND

    }


    if (filtro.cidade) {

      // FUTURO BACKEND

    }


    return true

  })

})



/* ==========================================================
   DEPARTAMENTO SELECIONADO
========================================================== */

/*
 * null:
 * mostra o DepartamentosTable.
 *
 * objeto:
 * mostra o VisualizacaoDepartamento.
 */

const departamentoSelecionado = ref(null)

const mostrarNovoDepartamento = ref(false)



/* ==========================================================
   ABRIR NOVO DEPARTAMENTO
========================================================== */

function abrirNovoDepartamento() {

  mostrarNovoDepartamento.value = true

}



function salvarNovoDepartamento(novoDepartamento) {

  const nome = novoDepartamento.nome?.trim()

  if (!nome) return

  const sigla = novoDepartamento.sigla || nome
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map(item => item[0])
    .join('')
    .toUpperCase()

  departamentos.value.unshift({
    id: Date.now(),
    sigla,
    nome,
    descricao: `Departamento ${nome} cadastrado no sistema.`,
    bens: 0,
    consertos: 0,
    status: novoDepartamento.status || 'Ativo',
    cor: '#16A34A',
    corFundo: '#DCFCE7',
    escritorio: novoDepartamento.escritorio || null,
  })

  mostrarNovoDepartamento.value = false

}



/* ==========================================================
   ABRIR VISUALIZAÇÃO
========================================================== */

/*
 * Recebe o departamento enviado pelo
 * DepartamentosTable.vue.
 *
 * A partir daqui:
 *
 * DepartamentosTable
 *        ↓
 * desaparece
 *
 * VisualizacaoDepartamento
 *        ↓
 * aparece no mesmo espaço
 */

function abrirVisualizacaoDepartamento(departamento) {

  departamentoSelecionado.value = departamento

}



/* ==========================================================
   VOLTAR PARA DEPARTAMENTOS
========================================================== */

/*
 * Chamado pelo botão "← Departamentos"
 * dentro do VisualizacaoDepartamento.
 */

function voltarDepartamentos() {

  departamentoSelecionado.value = null

}

</script>