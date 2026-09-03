<template>

  

    <q-page class="visualizacao-departamento-page">

      <!-- =====================================================
           CABEÇALHO
      ====================================================== -->

      <div class="visualizacao-departamento-header">

        <!-- VOLTAR -->

        <div
          class="visualizacao-departamento-voltar"
          @click="voltar"
        >

          <q-icon
            name="arrow_back"
            size="18px"
          />

          <span>
            Departamentos
          </span>

        </div>


        <!-- =================================================
             IDENTIFICAÇÃO DO DEPARTAMENTO
        ================================================== -->

        <div class="visualizacao-departamento-identificacao">

          <!-- SIGLA -->

          <div class="visualizacao-departamento-sigla">
            {{ departamento.sigla }}
          </div>


          <!-- TÍTULO -->

          <div>

            <div class="visualizacao-departamento-titulo">
              {{ departamento.nome }}
            </div>

            <div class="visualizacao-departamento-subtitulo">
              Inventário completo de bens atribuídos a este departamento.
            </div>

          </div>

        </div>

      </div>


      <!-- =====================================================
           CARDS DE RESUMO
      ====================================================== -->

      <div class="visualizacao-departamento-resumo">


        <!-- TOTAL DE BENS -->

        <q-card
          flat
          bordered
          class="visualizacao-departamento-resumo-card"
        >

          <div class="visualizacao-departamento-resumo-icone total">

            <q-icon
              name="inventory_2"
              size="20px"
            />

          </div>

          <div class="visualizacao-departamento-resumo-numero">
            {{ resumo.totalBens }}
          </div>

          <div class="visualizacao-departamento-resumo-label">
            Total de Bens
          </div>

        </q-card>



        <!-- BENS ATIVOS -->

        <q-card
          flat
          bordered
          class="visualizacao-departamento-resumo-card"
        >

          <div class="visualizacao-departamento-resumo-icone ativos">

            <q-icon
              name="check_circle_outline"
              size="20px"
            />

          </div>

          <div class="visualizacao-departamento-resumo-numero">
            {{ resumo.bensAtivos }}
          </div>

          <div class="visualizacao-departamento-resumo-label">
            Bens Ativos
          </div>

        </q-card>



        <!-- EM MANUTENÇÃO -->

        <q-card
          flat
          bordered
          class="visualizacao-departamento-resumo-card"
        >

          <div class="visualizacao-departamento-resumo-icone manutencao">

            <q-icon
              name="error_outline"
              size="20px"
            />

          </div>

          <div class="visualizacao-departamento-resumo-numero">
            {{ resumo.emManutencao }}
          </div>

          <div class="visualizacao-departamento-resumo-label">
            Em Manutenção
          </div>

        </q-card>



        <!-- CONCERTOS -->

        <q-card
          flat
          bordered
          class="visualizacao-departamento-resumo-card"
        >

          <div class="visualizacao-departamento-resumo-icone concertos">

            <q-icon
              name="build"
              size="20px"
            />

          </div>

          <div class="visualizacao-departamento-resumo-numero">
            {{ resumo.concertos }}
          </div>

          <div class="visualizacao-departamento-resumo-label">
            Concertos
          </div>

        </q-card>

      </div>



      <!-- =====================================================
           FILTROS
      ====================================================== -->

      <div class="visualizacao-departamento-filtros">


        <!-- BUSCA -->

        <q-input
          v-model="filtros.busca"
          outlined
          dense
          clearable
          placeholder="Buscar por ID, descrição, nº de série ou responsável..."
          class="visualizacao-departamento-busca"
        >

          <template #prepend>

            <q-icon
              name="search"
              size="20px"
            />

          </template>

        </q-input>



        <!-- STATUS -->

        <q-select
          v-model="filtros.status"
          outlined
          dense
          clearable
          label="Status"
          :options="statusOptions"
          class="visualizacao-departamento-filtro"
        />



        <!-- CATEGORIA -->

        <q-select
          v-model="filtros.categoria"
          outlined
          dense
          clearable
          label="Categoria"
          :options="categoriaOptions"
          class="visualizacao-departamento-filtro"
        />

      </div>



      <!-- =====================================================
           LISTA DE BENS
      ====================================================== -->

      <q-card
        flat
        bordered
        class="visualizacao-departamento-bens"
      >


        <!-- CABEÇALHO DA LISTA -->

        <div class="visualizacao-departamento-bens-header">

          <div>

            <span class="visualizacao-departamento-bens-titulo">
              Bens do Departamento
            </span>

            <span class="visualizacao-departamento-bens-contador">
              {{ bensFiltrados.length }} registros
            </span>

          </div>


          <div class="visualizacao-departamento-bens-total">

            <q-icon
              name="inventory_2"
              size="16px"
            />

            {{ resumo.totalBens }} bens cadastrados no total

          </div>

        </div>



        <!-- TABELA -->

        <q-table
          flat
          :rows="bensFiltrados"
          :columns="columns"
          row-key="id"
          hide-pagination
          :rows-per-page-options="[0]"
          class="visualizacao-departamento-table"
        >

          <!-- ID -->

          <template #body-cell-id="props">

            <q-td :props="props">

              <div class="visualizacao-departamento-id">
                {{ props.row.id }}
              </div>

            </q-td>

          </template>



          <!-- DESCRIÇÃO -->

          <template #body-cell-descricao="props">

            <q-td :props="props">

              <div class="visualizacao-departamento-descricao">

                <div class="visualizacao-departamento-produto-icone">

                  <q-icon
                    name="inventory_2"
                    size="16px"
                  />

                </div>

                <span>
                  {{ props.row.descricao }}
                </span>

              </div>

            </q-td>

          </template>



          <!-- CATEGORIA -->

          <template #body-cell-categoria="props">

            <q-td :props="props">

              <q-badge
                color="grey-3"
                text-color="grey-8"
                class="visualizacao-departamento-categoria"
              >
                {{ props.row.categoria }}
              </q-badge>

            </q-td>

          </template>



          <!-- RESPONSÁVEL -->

          <template #body-cell-responsavel="props">

            <q-td :props="props">

              <div class="visualizacao-departamento-responsavel">

                <div class="visualizacao-departamento-avatar">
                  {{ iniciais(props.row.responsavel) }}
                </div>

                {{ props.row.responsavel }}

              </div>

            </q-td>

          </template>



          <!-- STATUS -->

          <template #body-cell-status="props">

            <q-td :props="props">

              <q-badge
                :color="props.row.status === 'Ativo' ? 'green-1' : 'orange-1'"
                :text-color="props.row.status === 'Ativo' ? 'green-8' : 'orange-8'"
                class="visualizacao-departamento-status"
              >
                {{ props.row.status }}
              </q-badge>

            </q-td>

          </template>



          <!-- LOCALIZAÇÃO -->

          <template #body-cell-localizacao="props">

            <q-td :props="props">

              {{ props.row.localizacao }}

            </q-td>

          </template>



          <!-- ÚLTIMA MOVIMENTAÇÃO -->

          <template #body-cell-ultimaMovimentacao="props">

            <q-td :props="props">

              {{ props.row.ultimaMovimentacao }}

            </q-td>

          </template>



          <!-- AÇÕES -->

          <template #body-cell-acoes="props">

            <q-td :props="props">

              <div class="visualizacao-departamento-acoes">

                <q-btn
                  flat
                  round
                  dense
                  icon="visibility"
                  size="sm"
                  @click="abrirDetalhesBem(props.row)"
                />

                <q-btn
                  flat
                  round
                  dense
                  icon="edit"
                  size="sm"
                  @click="abrirEdicaoBem(props.row)"
                />

                <q-btn
                  flat
                  round
                  dense
                  icon="delete"
                  size="sm"
                  color="negative"
                  @click="abrirExclusaoBem(props.row)"
                />

              </div>

            </q-td>

          </template>

        </q-table>

      </q-card>

      <BemDetailsDialog
        v-model="dialogDetalhesBem"
        :bem="bemDetalhes"
      />

      <BemCadastroDialog
        v-model="dialogEdicaoBem"
        :bem="bemEditando"
        @salvo="salvarBemDepartamento"
      />

      <DeleteDialog
        v-model="dialogDeleteBem"
        :bem="bemExcluido"
        @remover-bem="removerBemDepartamento"
      />

    </q-page>

</template>

<script setup>

import { ref, computed } from 'vue'
import BemDetailsDialog from '@/components/bens/cards/BensDialogs.vue'
import BemCadastroDialog from '@/components/bens/cadastro/Cadastro.vue'
import DeleteDialog from '@/components/bens/cards/DeleteDialog.vue'


/* ==========================================================
   PROPS
========================================================== */

/*
 * Recebe o departamento selecionado
 * pela DepartamentosPage.vue.
 *
 * Fluxo:
 *
 * DepartamentosTable
 *        ↓
 * usuário clica
 *        ↓
 * DepartamentosPage
 *        ↓
 * departamentoSelecionado
 *        ↓
 * VisualizacaoDepartamento
 *
 *
 * FUTURO BACKEND:
 *
 * O departamento poderá vir diretamente
 * da API do Spring Boot.
 */

const props = defineProps({

  departamento: {

    type: Object,

    default: () => ({

      id: null,

      sigla: '',

      nome: '',

      descricao: '',

      bens: 0,

      consertos: 0,

      status: 'Ativo'

    })

  }

})


/* ==========================================================
   EMITS
========================================================== */

/*
 * Solicita que a DepartamentosPage.vue
 * volte para a lista de departamentos.
 */

const emit = defineEmits([

  'voltar'

])

const dialogDetalhesBem = ref(false)
const dialogEdicaoBem = ref(false)
const dialogDeleteBem = ref(false)
const bemDetalhes = ref({})
const bemEditando = ref(null)
const bemExcluido = ref(null)

function voltar() {

  emit('voltar')

}

function abrirDetalhesBem(bem) {
  bemDetalhes.value = { ...bem }
  dialogDetalhesBem.value = true
}

function abrirEdicaoBem(bem) {
  bemEditando.value = { ...bem }
  dialogEdicaoBem.value = true
}

function abrirExclusaoBem(bem) {
  bemExcluido.value = { ...bem }
  dialogDeleteBem.value = true
}

function salvarBemDepartamento(dados) {
  bens.value = bens.value.map(item => {
    if (item.id !== dados.id) return item

    return {
      ...item,
      ...dados,
      nome: dados.nome || item.nome || item.descricao,
      descricao: dados.descricao || item.descricao || item.nome,
      status: dados.status || item.status,
    }
  })

  dialogEdicaoBem.value = false
}

function removerBemDepartamento(id) {
  bens.value = bens.value.filter(item => item.id !== id)
  dialogDeleteBem.value = false
}



/* ==========================================================
   RESUMO DO DEPARTAMENTO
========================================================== */

/*
 * Estes dados são MOCKS temporários.
 *
 * Eles representam os dados que futuramente
 * poderão ser retornados pela API.
 *
 * FUTURO BACKEND:
 *
 * GET /api/departamentos/{id}
 *
 * Poderá retornar:
 *
 * - quantidade total de bens
 * - bens ativos
 * - bens em manutenção
 * - quantidade de consertos
 */

const resumo = computed(() => {

  return {

    totalBens:
      props.departamento?.bens || 0,

    bensAtivos:
      2,

    emManutencao:
      1,

    consertos:
      props.departamento?.consertos || 0

  }

})



/* ==========================================================
   FILTROS
========================================================== */

/*
 * Filtros utilizados na lista de bens
 * pertencentes ao departamento.
 */

const filtros = ref({

  busca: '',

  status: null,

  categoria: null

})



/* ==========================================================
   OPÇÕES DOS FILTROS
========================================================== */

const statusOptions = [

  'Ativo',

  'Inativo',

  'Manutenção'

]


const categoriaOptions = [

  'Tecnologia',

  'Informática',

  'Periféricos',

  'Infraestrutura'

]



/* ==========================================================
   MOCK DOS BENS
========================================================== */

/*
 * MOCKS temporários.
 *
 * Estes bens representam os equipamentos
 * associados ao departamento selecionado.
 *
 * FUTURO BACKEND:
 *
 * GET /api/departamentos/{id}/bens
 *
 * O resultado da API substituirá este array.
 */

const bens = ref([

  {

    id: 'BEM-1001',
    nome: 'Notebook Dell Latitude 7440',
    descricao: 'Notebook corporativo para uso administrativo e desenvolvimento.',
    categoria: 'Tecnologia',
    codigo: 'DCL-7440-01',
    patrimonio: 'PAT-2024-1001',
    serie: 'SN-DEL-7440-001',
    fabricante: 'Dell',
    modelo: 'Latitude 7440',
    responsavel: 'Ricardo Alves',
    status: 'Ativo',
    dataAquisicao: '2024-02-15',
    escritorio: 'Sede',
    departamento: 'Assessoria da Diretoria',
    localizacao: 'Sala AD – Sede',
    ultimaMovimentacao: '12/05/2025',
    icon: 'laptop',
    avatarColor: 'blue-1',
    iconColor: 'blue-8'

  },


  {

    id: 'BEM-1002',
    nome: 'Computador Dell OptiPlex',
    descricao: 'Desktop de produtividade para atendimento interno.',
    categoria: 'Tecnologia',
    codigo: 'DOP-001',
    patrimonio: 'PAT-2024-1002',
    serie: 'SN-DEL-OP-002',
    fabricante: 'Dell',
    modelo: 'OptiPlex 7090',
    responsavel: 'Ricardo Alves',
    status: 'Ativo',
    dataAquisicao: '2023-10-18',
    escritorio: 'Sede',
    departamento: 'Assessoria da Diretoria',
    localizacao: 'Sala AD – Sede',
    ultimaMovimentacao: '08/04/2025',
    icon: 'desktop_windows',
    avatarColor: 'green-1',
    iconColor: 'green-8'

  },


  {

    id: 'BEM-1003',
    nome: 'Monitor LG 24"',
    descricao: 'Monitor para estação de trabalho e reuniões.',
    categoria: 'Periféricos',
    codigo: 'LG-24-001',
    patrimonio: 'PAT-2024-1003',
    serie: 'SN-LG-24-003',
    fabricante: 'LG',
    modelo: '24MK430',
    responsavel: 'Ricardo Alves',
    status: 'Manutenção',
    dataAquisicao: '2022-11-09',
    escritorio: 'Sede',
    departamento: 'Assessoria da Diretoria',
    localizacao: 'Sala AD – Sede',
    ultimaMovimentacao: '20/03/2025',
    icon: 'monitor',
    avatarColor: 'orange-1',
    iconColor: 'orange-8'

  }

])



/* ==========================================================
   COLUNAS
========================================================== */

const columns = [

  {

    name: 'id',

    label: 'ID',

    field: 'id',

    align: 'left'

  },


  {

    name: 'descricao',

    label: 'DESCRIÇÃO',

    field: 'descricao',

    align: 'left'

  },


  {

    name: 'categoria',

    label: 'CATEGORIA',

    field: 'categoria',

    align: 'left'

  },


  {

    name: 'marcaModelo',

    label: 'MARCA / MODELO',

    field: 'marcaModelo',

    align: 'left'

  },


  {

    name: 'responsavel',

    label: 'RESPONSÁVEL',

    field: 'responsavel',

    align: 'left'

  },


  {

    name: 'status',

    label: 'STATUS',

    field: 'status',

    align: 'left'

  },


  {

    name: 'localizacao',

    label: 'LOCALIZAÇÃO',

    field: 'localizacao',

    align: 'left'

  },


  {

    name: 'ultimaMovimentacao',

    label: 'ÚLT. MOVIMENTAÇÃO',

    field: 'ultimaMovimentacao',

    align: 'left'

  },


  {

    name: 'acoes',

    label: 'AÇÕES',

    field: 'acoes',

    align: 'center'

  }

]



/* ==========================================================
   FILTRAGEM DOS BENS
========================================================== */

/*
 * Filtra os bens apresentados no departamento.
 *
 * A busca procura por:
 *
 * - ID
 * - descrição
 * - categoria
 * - marca/modelo
 * - responsável
 */

const bensFiltrados = computed(() => {

  const busca = filtros.value.busca
    ?.toLowerCase()
    .trim()


  return bens.value.filter(item => {


    /* =====================================================
       BUSCA
    ====================================================== */

    if (busca) {

      const texto = `

        ${item.id}

        ${item.descricao}

        ${item.categoria}

        ${item.marcaModelo}

        ${item.responsavel}

      `.toLowerCase()


      if (!texto.includes(busca)) {

        return false

      }

    }


    /* =====================================================
       STATUS
    ====================================================== */

    if (

      filtros.value.status &&

      item.status !==
      filtros.value.status

    ) {

      return false

    }


    /* =====================================================
       CATEGORIA
    ====================================================== */

    if (

      filtros.value.categoria &&

      item.categoria !==
      filtros.value.categoria

    ) {

      return false

    }


    return true

  })

})



/* ==========================================================
   INICIAIS
========================================================== */

/*
 * Utilizado para gerar as iniciais
 * do responsável.
 */

function iniciais(nome) {

  if (!nome) {

    return ''

  }


  return nome

    .split(' ')

    .map(parte => parte[0])

    .slice(0, 2)

    .join('')

    .toUpperCase()

}



/* ==========================================================
   VOLTAR
========================================================== */

/*
 * Retorna para a lista de departamentos.
 *
 * A DepartamentosPage.vue recebe o evento
 * e define:
 *
 * departamentoSelecionado.value = null
 */

// function voltar() {

//   emit('voltar')

// }

</script>