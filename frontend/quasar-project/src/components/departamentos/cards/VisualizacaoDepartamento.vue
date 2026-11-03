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
            {{ resumo.consertos }}
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

        <q-banner v-if="erroBens" dense rounded class="bg-red-1 text-negative q-mb-md">
          {{ erroBens }}
          <template #action><q-btn flat color="negative" label="Tentar novamente" @click="carregarBens" /></template>
        </q-banner>
        <q-inner-loading :showing="carregandoBens"><q-spinner size="36px" color="primary" /></q-inner-loading>
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

              </div>

            </q-td>

          </template>

        </q-table>

      </q-card>

      <BemDetailsDialog
        v-model="dialogDetalhesBem"
        :bem="bemDetalhes"
      />

    </q-page>

</template>

<script setup>

import { ref, computed, watch } from 'vue'
import { api } from '@/boot/axios.js'
import BemDetailsDialog from '@/components/bens/cards/BensDialogs.vue'


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
const bemDetalhes = ref({})
const carregandoBens = ref(false)
const erroBens = ref('')

function voltar() {

  emit('voltar')

}

function abrirDetalhesBem(bem) {
  bemDetalhes.value = { ...bem }
  dialogDetalhesBem.value = true
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
  const statusNormalizado = (status) => String(status || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
  return {
    totalBens: bens.value.length,
    bensAtivos: bens.value.filter((item) => statusNormalizado(item.status) === 'ativo').length,
    emManutencao: bens.value.filter((item) => statusNormalizado(item.status).includes('manutenc')).length,
    consertos: props.departamento?.consertos || 0,
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



const bens = ref([])

async function carregarBens() {
  const id = props.departamento?.id
  if (!id) { bens.value = []; return }
  carregandoBens.value = true
  erroBens.value = ''
  try {
    const { data } = await api.get(`/departamentos/${id}/bens`)
    bens.value = data.map((item) => ({
      ...item,
      descricao: item.descricao || item.nome || '',
      marcaModelo: [item.fabricante, item.modelo].filter(Boolean).join(' / ') || '—',
      responsavel: item.responsavel || '—',
      localizacao: [item.escritorio, item.departamento].filter(Boolean).join(' / ') || '—',
      ultimaMovimentacao: '—',
    }))
  } catch (error) {
    erroBens.value = error.response?.data?.mensagem || 'Não foi possível carregar os bens deste departamento.'
  } finally { carregandoBens.value = false }
}

watch(() => props.departamento?.id, carregarBens, { immediate: true })

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

