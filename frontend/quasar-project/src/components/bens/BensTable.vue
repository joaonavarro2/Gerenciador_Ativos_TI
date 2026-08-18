<template>

  <q-card flat bordered class="bens-table">

    <!-- =======================================================
         CABEÇALHO DA TABELA
         ======================================================= -->

    <div class="table-header">

      <div class="table-title">

        <div class="table-title-main">
          Bens Cadastrados
        </div>

        <div class="table-title-sub">
          {{ bens.length }} registro(s)
        </div>

      </div>

      <div class="table-last-update">

        <q-icon name="schedule" size="16px" />

        Atualizado agora

      </div>

    </div>


    <!-- =======================================================
         TABELA
         ======================================================= -->

    <q-table
      flat
      :rows="bens"
      :columns="columns"
      row-key="id"
    >

      <template #body="props">

        <q-tr :props="props">

          <!-- ==============================
               ID
          ============================== -->

          <q-td key="id" :props="props">

            <q-badge class="badge-id">
              {{ props.row.id }}
            </q-badge>

          </q-td>


          <!-- ==============================
               DESCRIÇÃO
          ============================== -->

          <q-td key="descricao" :props="props">

            <div class="descricao-cell">

              <q-avatar
                rounded
                :color="props.row.avatarColor"
                :text-color="props.row.iconColor"
                size="38px"
              >

                <q-icon
                  :name="props.row.icon"
                  size="18px"
                />

              </q-avatar>

              <div class="descricao-title">

                {{ props.row.descricao }}

              </div>

            </div>

          </q-td>


          <!-- ==============================
               CATEGORIA
          ============================== -->

          <q-td key="categoria" :props="props">

            <q-chip dense square class="categoria-chip">

              {{ props.row.categoria }}

            </q-chip>

          </q-td>


          <!-- ==============================
               MARCA
          ============================== -->

          <q-td key="marca" :props="props">

            {{ props.row.marca }}

          </q-td>


          <!-- ==============================
               DEPARTAMENTO
          ============================== -->

          <q-td key="departamento" :props="props">

            {{ props.row.departamento }}

          </q-td>


          <!-- ==============================
               RESPONSÁVEL
          ============================== -->

          <q-td key="responsavel" :props="props">

            <div class="responsavel-cell">

              <q-avatar
                size="28px"
                color="blue-1"
                text-color="primary"
              >

                <q-icon
                  name="person"
                  size="15px"
                />

              </q-avatar>

              {{ props.row.responsavel }}

            </div>

          </q-td>


          <!-- ==============================
               STATUS
          ============================== -->

          <q-td key="status" :props="props">

            <q-chip
              dense
              :class="statusClass(props.row.status)"
            >

              {{ props.row.status }}

            </q-chip>

          </q-td>


          <!-- ==============================
               AÇÕES
          ============================== -->

          <q-td
            key="acoes"
            :props="props"
            class="text-center"
          >

            <!-- VISUALIZAR -->

            <q-btn
              flat
              round
              dense
              class="acao-btn"
              @click="visualizarBem(props.row.id)"
            >

              <q-icon
                name="visibility"
                size="18px"
              />

            </q-btn>


            <!-- MOVIMENTAR -->

            <q-btn
              flat
              round
              dense
              class="acao-btn"
              @click="movimentarBem(props.row.id)"
            >

              <q-icon
                name="sync_alt"
                size="18px"
              />

            </q-btn>


            <!-- EDITAR -->

            <q-btn
              flat
              round
              dense
              class="acao-btn"
              @click="editarBem(props.row)"
            >

              <q-icon
                name="edit"
                size="18px"
              />

              <q-tooltip>
                Editar bem
              </q-tooltip>

            </q-btn>


            <!-- EXCLUIR -->

            <q-btn
              flat
              round
              dense
              class="acao-btn"
              @click="deletarBem(props.row.id)"
            >

              <q-icon
                name="delete"
                size="18px"
              />

              <q-tooltip>
                Excluir bem
              </q-tooltip>

            </q-btn>

          </q-td>

        </q-tr>

      </template>

    </q-table>


    <!-- =======================================================
         RODAPÉ
         ======================================================= -->

    <div class="table-footer">

      <div class="footer-left">

        Exibindo

        {{ bens.length }}

        de

        {{ bens.length }}

        bens

      </div>


      <div class="footer-right">

        <q-chip dense class="status-ativo">

          Ativo:
          {{ totalAtivos }}

        </q-chip>


        <q-chip dense class="status-manutencao">

          Em Manutenção:
          {{ totalManutencao }}

        </q-chip>


        <q-chip dense class="status-inativo">

          Inativo:
          {{ totalInativos }}

        </q-chip>


        <q-chip dense class="status-descartado">

          Descartado:
          {{ totalDescartados }}

        </q-chip>

      </div>

    </div>

  </q-card>


  <!-- =======================================================
       MODAL DE DETALHES
       ======================================================= -->

  <BemDetailsDialog
    v-model="dialogDetalhes"
    :bem-id="bemSelecionado"
  />


  <!-- =======================================================
       MODAL DE MOVIMENTAÇÃO
       ======================================================= -->

  <MovimentacaoRegister
    v-model="dialogMovimentacao"
    :bem-id="bemMovimentacao"
  />


  <!-- =======================================================
       MODAL DE EXCLUSÃO
       ======================================================= -->

  <DeleteDialog
    v-model="dialogDelete"
    :bem="bemExcluir"
    @remover-bem="removerBem"
  />


  <!-- =======================================================
       MODAL DE CADASTRO / EDIÇÃO
       ======================================================= -->

  <BemCadastroDialog
    v-model="dialogCadastro"
    :bem="bemEditando"
    @salvo="bemSalvo"
  />

</template>


<script setup>

import {
  ref,
  computed
} from 'vue'


import BemDetailsDialog
  from './cards/BensDialogs.vue'

import MovimentacaoRegister
  from './cards/MovimentacaoRegister.vue'

import DeleteDialog
  from './cards/DeleteDialog.vue'

import BemCadastroDialog
  from './cadastro/Cadastro.vue'


/* ==========================================================
   MOCKS
========================================================== */

/* Dados migrados para BensPage.vue.
const bens = ref([

  {
    id: 'BEM-001',
    descricao: 'Trator Agrícola MF 275',
    serie: 'MF275-2023-001',
    categoria: 'Veículo',
    marca: 'Massey Ferguson • MF 275',
    departamento: 'Operações',
    responsavel: 'Carlos Andrade',
    status: 'Inativo',
    icon: 'agriculture',
    avatarColor: 'green-1',
    iconColor: 'green-8'
  },

  {
    id: 'BEM-002',
    descricao: 'Lenovo ThinkPad X1 Carbon',
    serie: 'LEN-X1-2024-001',
    categoria: 'Notebook',
    marca: 'Lenovo • ThinkPad X1 Carbon',
    departamento: 'Dep. Recursos Humanos',
    responsavel: 'Mariana Santos',
    status: 'Ativo',
    icon: 'laptop',
    avatarColor: 'blue-1',
    iconColor: 'blue-8'
  },

  {
    id: 'BEM-003',
    descricao: 'Dell Latitude 7420',
    serie: 'DEL-7420-001',
    categoria: 'Notebook',
    marca: 'Dell • Latitude 7420',
    departamento: 'Dep. Financeiro',
    responsavel: 'João Silva',
    status: 'Em Manutenção',
    icon: 'laptop',
    avatarColor: 'orange-1',
    iconColor: 'orange-8'
  },

  {
    id: 'BEM-004',
    descricao: 'Monitor Samsung 24"',
    serie: 'SAM-MON-001',
    categoria: 'Monitor',
    marca: 'Samsung • T350',
    departamento: 'Dep. Administrativo',
    responsavel: 'Ana Oliveira',
    status: 'Ativo',
    icon: 'monitor',
    avatarColor: 'purple-1',
    iconColor: 'purple-8'
  },

  {
    id: 'BEM-005',
    descricao: 'Impressora Brother HL',
    serie: 'BRO-HL-001',
    categoria: 'Impressora',
    marca: 'Brother • HL-L2360DW',
    departamento: 'Dep. Licitações',
    responsavel: 'Pedro Souza',
    status: 'Inativo',
    icon: 'print',
    avatarColor: 'grey-3',
    iconColor: 'grey-8'
  },

  {
    id: 'BEM-006',
    descricao: 'Scanner Canon',
    serie: 'CAN-SCAN-001',
    categoria: 'Scanner',
    marca: 'Canon • DR-C240',
    departamento: 'Controle Interno',
    responsavel: 'Lucas Almeida',
    status: 'Ativo',
    icon: 'document_scanner',
    avatarColor: 'cyan-1',
    iconColor: 'cyan-8'
  },

  {
    id: 'BEM-007',
    descricao: 'Computador Dell OptiPlex',
    serie: 'DEL-OPT-001',
    categoria: 'PC',
    marca: 'Dell • OptiPlex 7090',
    departamento: 'Diretoria Geral',
    responsavel: 'Fernanda Costa',
    status: 'Descartado',
    icon: 'desktop_windows',
    avatarColor: 'red-1',
    iconColor: 'red-8'
  },

  {
    id: 'BEM-008',
    descricao: 'Notebook HP ProBook',
    serie: 'HP-PRO-001',
    categoria: 'Notebook',
    marca: 'HP • ProBook 450',
    departamento: 'Dep. Engenharia',
    responsavel: 'Ricardo Lima',
    status: 'Ativo',
    icon: 'laptop',
    avatarColor: 'indigo-1',
    iconColor: 'indigo-8'
  },

  {
    id: 'BEM-009',
    descricao: 'Monitor LG UltraWide',
    serie: 'LG-ULT-001',
    categoria: 'Monitor',
    marca: 'LG • UltraWide 29"',
    departamento: 'Assessoria Jurídica',
    responsavel: 'Juliana Martins',
    status: 'Em Manutenção',
    icon: 'monitor',
    avatarColor: 'teal-1',
    iconColor: 'teal-8'
  }

])
*/

const props = defineProps({
  bens: {
    type: Array,
    default: () => []
  }
})

const bens = computed(() => props.bens)


/* ==========================================================
   EVENTO
========================================================== */

const emit = defineEmits([
  'bens-atualizados'
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
    name: 'marca',
    label: 'MARCA / MODELO',
    field: 'marca',
    align: 'left'
  },

  {
    name: 'departamento',
    label: 'DEPARTAMENTO',
    field: 'departamento',
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
    name: 'acoes',
    label: 'AÇÕES',
    align: 'center'
  }

]


/* ==========================================================
   STATUS
========================================================== */

function statusClass(status) {

  switch (status) {

    case 'Ativo':
      return 'status-chip status-ativo'

    case 'Em Manutenção':
      return 'status-chip status-manutencao'

    case 'Inativo':
      return 'status-chip status-inativo'

    case 'Descartado':
      return 'status-chip status-descartado'

    default:
      return 'status-chip'

  }

}


/* ==========================================================
   RESUMO
========================================================== */

const totalAtivos = computed(() =>
  bens.value.filter(
    b => b.status === 'Ativo'
  ).length
)

const totalManutencao = computed(() =>
  bens.value.filter(
    b => b.status === 'Em Manutenção'
  ).length
)

const totalInativos = computed(() =>
  bens.value.filter(
    b => b.status === 'Inativo'
  ).length
)

const totalDescartados = computed(() =>
  bens.value.filter(
    b => b.status === 'Descartado'
  ).length
)


/* ==========================================================
   DIALOGS
========================================================== */

const dialogDetalhes = ref(false)
const bemSelecionado = ref(null)

const dialogMovimentacao = ref(false)
const bemMovimentacao = ref(null)

const dialogDelete = ref(false)
const bemExcluir = ref(null)


/* ==========================================================
   EDITAR
========================================================== */

/*
 * Bem que será enviado para o modal
 * de cadastro/edição.
 */

const dialogCadastro = ref(false)

const bemEditando = ref(null)


function editarBem(bem) {

  /*
   * Guarda o objeto completo do bem.
   *
   * O CadastroDialog receberá esse objeto
   * através da prop :bem.
   */

  bemEditando.value = {
    ...bem
  }

  /*
   * Abre o modal.
   */

  dialogCadastro.value = true


  /*
   =========================================================
   BACKEND — FUTURO
   =========================================================

   Atualmente:

   MOCK
     ↓
   bens.value
     ↓
   bemEditando
     ↓
   BemCadastroDialog

   Futuramente:

   GET /api/bens/{id}
     ↓
   Spring Boot
     ↓
   Axios
     ↓
   bemEditando
     ↓
   BemCadastroDialog

   =========================================================
   */

}


/* ==========================================================
   BEM SALVO
========================================================== */

function bemSalvo(bemAtualizado) {

  /*
   * Procura o bem antigo pelo ID.
   */

  const bensAtualizados = bens.value.map(bem =>
    bem.id === bemAtualizado.id
      ? { ...bem, ...bemAtualizado }
      : bem
  )


  /*
   * Atualiza quem estiver escutando
   * a lista de bens.
   */

  emit(
    'bens-atualizados',
    bensAtualizados
  )

}


/* ==========================================================
   VISUALIZAR
========================================================== */

function visualizarBem(id) {

  /*
   =========================================================
   BACKEND — FUTURO

   GET /api/bens/{id}

   =========================================================
   */

  bemSelecionado.value = id

  dialogDetalhes.value = true

}


/* ==========================================================
   MOVIMENTAR
========================================================== */

function movimentarBem(id) {

  /*
   =========================================================
   BACKEND — FUTURO

   POST /api/movimentacoes

   =========================================================
   */

  bemMovimentacao.value = id

  dialogMovimentacao.value = true

}


/* ==========================================================
   EXCLUIR
========================================================== */

function deletarBem(id) {

  bemExcluir.value = bens.value.find(
    bem => bem.id === id
  )

  dialogDelete.value = true

}


/* ==========================================================
   REMOVER MOCK
========================================================== */

function removerBem(id) {

  /*
   =========================================================
   BACKEND — FUTURO

   DELETE /api/bens/{id}

   =========================================================
   */

  const bensAtualizados = bens.value.filter(
    bem => bem.id !== id
  )


  emit(
    'bens-atualizados',
    bensAtualizados
  )

}

</script>
