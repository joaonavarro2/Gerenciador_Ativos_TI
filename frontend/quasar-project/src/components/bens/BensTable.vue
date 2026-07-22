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

    <q-table flat :rows="bens" :columns="columns" row-key="id" 
>

      <!-- ==============================
           ID
      ============================== -->

      <template #body="props">
        <q-tr :props="props">
          <q-td key="id" :props="props">

            <q-badge class="badge-id">
              {{ props.row.id }}
            </q-badge>

          </q-td>



          <!-- ==============================
           DESCRIÇÃO
      ============================== -->

          <q-td key="descricao">

            <div class="descricao-cell">

              <q-avatar rounded :color="props.row.avatarColor" :text-color="props.row.iconColor" size="38px">
                <q-icon :name="props.row.icon" size="18px" />
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

          <q-td key="marca">

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

          <q-td key="responsavel">

            <div class="responsavel-cell">

              <q-avatar size="28px" color="blue-1" text-color="primary">
                <q-icon name="person" size="15px" />
              </q-avatar>

              {{ props.row.responsavel }}

            </div>

          </q-td>

          <!-- ==============================
           STATUS
      ============================== -->

          <q-td key="status" :props="props">

            <q-chip dense :class="statusClass(props.row.status)">
              {{ props.row.status }}
            </q-chip>

          </q-td>

          <!-- ==============================
           AÇÕES
      ============================== -->

          <q-td key="acoes" :props="props" class="text-center">

            <q-btn flat round dense class="acao-btn" @click="visualizarBem(props.row.id)">

              <q-icon name="visibility" size="18px" />

            </q-btn>

            <q-btn flat round dense class="acao-btn" @click="movimentarBem(props.row.id)">

              <q-icon name="sync_alt" size="18px" />

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

</template>








<script setup>
import { ref,computed } from 'vue'

/* ==========================================================
   BACKEND
   ==========================================================
 
GET /api/bens
 
Resposta esperada:
 
[
  {
    id,
    descricao,
    serie,
    categoria,
    marca,
    departamento,
    responsavel,
    status,
    icon,
    avatarColor,
    iconColor
  }
]
 
Quando o backend estiver pronto,
remover o exemplo abaixo e preencher
o array utilizando a API.
 
========================================================== */


const bens = ref([
  {
    id: 'BEM-0041',
    descricao: 'Trator Agrícola MF 275',
    serie: 'MF275-2023-001',
    categoria: 'Veículo',
    marca: 'Massey Ferguson • MF 275',
    departamento: 'Operações',
    responsavel: 'Carlos Andrade',
    status: 'Ativo',
    icon: 'agriculture',
    avatarColor: 'green-1',
    iconColor: 'green-8'
  }
])
/* ==========================================================
   COLUNAS DA TABELA
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
   RESUMO DO RODAPÉ
========================================================== */

const totalAtivos = computed(() =>
  bens.value.filter(b => b.status === 'Ativo').length
)

const totalManutencao = computed(() =>
  bens.value.filter(b => b.status === 'Em Manutenção').length
)

const totalInativos = computed(() =>
  bens.value.filter(b => b.status === 'Inativo').length
)

const totalDescartados = computed(() =>
  bens.value.filter(b => b.status === 'Descartado').length
)

/* ==========================================================
   BOTÕES
========================================================== */

function visualizarBem(id) {

  /*
  =====================================
 
  BACKEND
 
  GET /api/bens/{id}
 
  Abrirá o modal de visualização
 
  =====================================
  */

  console.log('Visualizar', id)

}

function movimentarBem(id) {

  /*
  =====================================
 
  BACKEND
 
  POST /api/movimentacoes
 
  Abrirá o modal de movimentação
 
  =====================================
  */

  console.log('Movimentar', id)

}

</script>