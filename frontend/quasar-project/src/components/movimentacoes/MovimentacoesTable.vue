
<template>

  <q-card
    flat
    bordered
    class="movimentacoes-body"
  >

    <!-- =====================================================
         CABEÇALHO DA TABELA
    ====================================================== -->

    <div class="movimentacoes-body-header">

      <div class="movimentacoes-body-title">

        <span class="movimentacoes-body-title-main">
          Histórico de Movimentações
        </span>

        <span class="movimentacoes-body-title-count">
          {{ movimentacoes.length }} registros
        </span>

      </div>


      <div class="movimentacoes-body-atualizacao">

        <q-icon
          name="schedule"
          size="14px"
        />

        <span>
          Atualizado agora
        </span>

      </div>

    </div>


    <!-- =====================================================
         TABELA
    ====================================================== -->

    <q-table
      flat
      :rows="movimentacoes"
      :columns="columns"
      row-key="id"
      class="movimentacoes-table"
      hide-pagination
      :rows-per-page-options="[8]"
    >


      <!-- ===================================================
           ID DO BEM
      ==================================================== -->

      <template #body-cell-idBem="props">

        <q-td :props="props">

          <div class="movimentacoes-id-bem">

            <div class="movimentacoes-patrimonio">
              {{ props.row.patrimonio }}
            </div>

            <div class="movimentacoes-id">
              {{ props.row.codigoMovimentacao }}
            </div>

          </div>

        </q-td>

      </template>


      <!-- ===================================================
           BEM
      ==================================================== -->

      <template #body-cell-bem="props">

        <q-td :props="props">

          <div class="movimentacoes-nome-bem">
            {{ props.row.bem }}
          </div>

        </q-td>

      </template>


      <!-- ===================================================
           TIPO
      ==================================================== -->

      <template #body-cell-tipo="props">

        <q-td :props="props">

          <q-chip
            dense
            class="movimentacoes-tipo-chip"
            :class="`movimentacoes-tipo-${props.row.tipoClasse}`"
          >

            <q-icon
              :name="props.row.tipoIcone"
              size="14px"
              class="q-mr-xs"
            />

            {{ props.row.tipo }}

          </q-chip>

        </q-td>

      </template>


      <!-- ===================================================
           ORIGEM
      ==================================================== -->

      <template #body-cell-origem="props">

        <q-td :props="props">

          <div class="movimentacoes-local">

            <span class="movimentacoes-local-ponto"></span>

            {{ props.row.origem }}

          </div>

        </q-td>

      </template>


      <!-- ===================================================
           DESTINO
      ==================================================== -->

      <template #body-cell-destino="props">

        <q-td :props="props">

          <div class="movimentacoes-local">

            <q-icon
              name="sync_alt"
              size="15px"
              class="movimentacoes-destino-icone"
            />

            {{ props.row.destino }}

          </div>

        </q-td>

      </template>


      <!-- ===================================================
           DATA
      ==================================================== -->

      <template #body-cell-data="props">

        <q-td :props="props">

          <span class="movimentacoes-data">
            {{ props.row.data }}
          </span>

        </q-td>

      </template>


      <!-- ===================================================
           RESPONSÁVEL
      ==================================================== -->

      <template #body-cell-responsavel="props">

        <q-td :props="props">

          <div class="movimentacoes-responsavel">

            <div class="movimentacoes-responsavel-avatar">
              {{ props.row.iniciais }}
            </div>

            <span>
              {{ props.row.responsavel }}
            </span>

          </div>

        </q-td>

      </template>


      <!-- ===================================================
           STATUS
      ==================================================== -->

      <template #body-cell-status="props">

        <q-td :props="props">

          <q-chip
            dense
            class="movimentacoes-status-chip"
            :class="`movimentacoes-status-${props.row.statusClasse}`"
          >

            <q-icon
              :name="props.row.statusIcone"
              size="14px"
              class="q-mr-xs"
            />

            {{ props.row.status }}

          </q-chip>

        </q-td>

      </template>


      <!-- ===================================================
           QUANDO NÃO EXISTIREM REGISTROS
      ==================================================== -->

      <template #no-data>

        <div class="movimentacoes-sem-dados">

          <q-icon
            name="swap_horiz"
            size="40px"
          />

          <div>
            Nenhuma movimentação encontrada.
          </div>

        </div>

      </template>

    </q-table>


    <!-- =====================================================
         RODAPÉ DA TABELA
    ====================================================== -->

    <div class="movimentacoes-body-footer">

      <div class="movimentacoes-registros">

        Exibindo
        <strong>{{ movimentacoes.length }}</strong>
        registros

      </div>


      <!-- PAGINAÇÃO -->

      <div class="movimentacoes-paginacao">

        <q-btn
          flat
          round
          dense
          icon="chevron_left"
          class="movimentacoes-paginacao-btn"
          disable
        />

        <q-btn
          unelevated
          dense
          label="1"
          class="movimentacoes-paginacao-atual"
        />

        <q-btn
          flat
          dense
          label="2"
          class="movimentacoes-paginacao-btn"
        />

        <q-btn
          flat
          round
          dense
          icon="chevron_right"
          class="movimentacoes-paginacao-btn"
        />

      </div>

    </div>

  </q-card>

</template>

<script setup>
import { computed } from 'vue'

/*
|--------------------------------------------------------------------------
| PROPS
|--------------------------------------------------------------------------
| A página principal será responsável por fornecer as movimentações.
|
| Atualmente os dados são mocks.
| Futuramente essa mesma lista virá do backend Java + Spring Boot.
|--------------------------------------------------------------------------
*/

const props = defineProps({
  movimentacoes: {
    type: Array,
    default: () => []
  }
})


/*
|--------------------------------------------------------------------------
| COLUNAS DA TABELA
|--------------------------------------------------------------------------
| Define a estrutura principal do q-table.
|
| Os nomes das colunas correspondem aos slots utilizados no template.
|--------------------------------------------------------------------------
*/

const columns = [
  {
    name: 'idBem',
    label: 'ID DO BEM',
    field: 'patrimonio',
    align: 'left',
    sortable: true
  },

  {
    name: 'bem',
    label: 'BEM',
    field: 'bem',
    align: 'left',
    sortable: true
  },

  {
    name: 'tipo',
    label: 'TIPO',
    field: 'tipo',
    align: 'left',
    sortable: true
  },

  {
    name: 'origem',
    label: 'ORIGEM',
    field: 'origem',
    align: 'left',
    sortable: true
  },

  {
    name: 'destino',
    label: 'DESTINO',
    field: 'destino',
    align: 'left',
    sortable: true
  },

  {
    name: 'data',
    label: 'DATA',
    field: 'data',
    align: 'left',
    sortable: true
  },

  {
    name: 'responsavel',
    label: 'RESPONSÁVEL',
    field: 'responsavel',
    align: 'left',
    sortable: true
  },

  {
    name: 'status',
    label: 'STATUS',
    field: 'status',
    align: 'left',
    sortable: true
  }
]


/*
|--------------------------------------------------------------------------
| DADOS DA TABELA
|--------------------------------------------------------------------------
| O template utiliza "movimentacoes".
|
| Como a lista vem através de props, usamos:
|
| props.movimentacoes
|
| Isso permite que Header e Body utilizem exatamente os mesmos dados.
|--------------------------------------------------------------------------
*/

const movimentacoes = computed(() => props.movimentacoes)
</script>
