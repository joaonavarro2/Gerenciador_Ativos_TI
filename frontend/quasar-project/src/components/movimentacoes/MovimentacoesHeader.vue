<!-- eslint-disable no-unused-vars -->
<template>
  <q-card flat bordered class="movimentacoes-header">

    <!-- =====================================================
         TOPO DA PÁGINA
    ====================================================== -->

    <div class="row items-start justify-between q-mb-lg">

      <!-- TÍTULO E DESCRIÇÃO -->
      <div class="movimentacoes-header-title">

        <div class="movimentacoes-title text-h4 text-weight-bold">
          Movimentações
        </div>

        <div class="movimentacoes-subtitle text-subtitle1 text-grey-7 q-mt-xs">
          Registro e controle de transferências entre unidades e departamentos.
        </div>

      </div>


      <!-- BOTÃO NOVA MOVIMENTAÇÃO -->
      <div class="movimentacoes-header-actions">

        <q-btn
          @click="novaMovimentacao"
          color="warning"
          icon="add"
          label="Nova Movimentação"
          no-caps
          unelevated
          class="movimentacoes-btn-nova"
        />

      </div>

    </div>


    <!-- =====================================================
         CARDS DE RESUMO
    ====================================================== -->

    <div class="row items-center q-gutter-md movimentacoes-resumo">

      <!-- TOTAL -->
      <q-card
        flat
        bordered
        class="movimentacoes-resumo-card"
      >

        <div class="movimentacoes-resumo-content">

          <div class="movimentacoes-resumo-numero movimentacoes-resumo-total">
              {{ resumoMovimentacoes.total }}
          </div>

          <div class="movimentacoes-resumo-label">
            Total
          </div>

        </div>

      </q-card>


      <!-- EM ABERTO -->
      <q-card
        flat
        bordered
        class="movimentacoes-resumo-card"
      >

        <div class="movimentacoes-resumo-content">

          <div class="movimentacoes-resumo-numero movimentacoes-resumo-aberto">
            {{ resumoMovimentacoes.emAberto }}
          </div>

          <div class="movimentacoes-resumo-label">
            Em Aberto
          </div>

        </div>

      </q-card>


      <!-- PENDENTE -->
      <q-card
        flat
        bordered
        class="movimentacoes-resumo-card"
      >

        <div class="movimentacoes-resumo-content">

          <div class="movimentacoes-resumo-numero movimentacoes-resumo-pendente">
            {{ resumoMovimentacoes.pendente }}
          </div>

          <div class="movimentacoes-resumo-label">
            Pendente
          </div>

        </div>

      </q-card>


      <!-- CONCLUÍDO -->
      <q-card
        flat
        bordered
        class="movimentacoes-resumo-card"
      >

        <div class="movimentacoes-resumo-content">

          <div class="movimentacoes-resumo-numero movimentacoes-resumo-concluido">
            {{ resumoMovimentacoes.concluido }}
            
          </div>

          <div class="movimentacoes-resumo-label">
            Concluído
          </div>

        </div>

      </q-card>

    </div>


    <!-- =====================================================
         FILTROS
    ====================================================== -->

    <div class="row q-col-gutter-md q-mt-lg movimentacoes-filtros">


      <!-- ===================================================
           PESQUISA
      ==================================================== -->

      <div class="col">

        <q-input
          v-model="filtros.busca"
          outlined
          dense
          clearable
          placeholder="Buscar por ID do bem, nome ou departamento..."
          class="movimentacoes-filtro-pesquisa"
        >

          <template #prepend>

            <q-icon name="search" />

          </template>

        </q-input>

      </div>


      <!-- ===================================================
           TIPO
      ==================================================== -->

      <div class="col-auto">

        <q-select
          v-model="filtros.tipo"
          outlined
          dense
          clearable
          label="Todos os Tipos"
          :options="[
            'Transferência',
            'Devolução',
            'Empréstimo',
            'Movimentação Interna'
          ]"
          class="movimentacoes-filtro-tipo"
        />

      </div>


      <!-- ===================================================
           DATA
      ==================================================== -->

      <div class="col-auto">

        <q-input
          v-model="filtros.data"
          outlined
          dense
          clearable
          placeholder="dd/mm/aaaa"
          class="movimentacoes-filtro-data"
        >

          <template #append>

            <q-icon
              name="event"
              class="cursor-pointer"
            />

          </template>

        </q-input>

      </div>


    </div>

  </q-card>
</template>

<script setup>
import { ref} from 'vue'

/*
|--------------------------------------------------------------------------
| FILTROS DA PÁGINA
|--------------------------------------------------------------------------
| Esses valores serão utilizados pelos componentes do template.
| Futuramente poderão ser enviados para a API do Spring Boot.
*/

const filtros = ref({
  busca: '',
  tipo: null,
  data: null
})


/*
|--------------------------------------------------------------------------
| RESUMO DAS MOVIMENTAÇÕES
|--------------------------------------------------------------------------
| Por enquanto são valores mockados.
|
| Futuramente esses números poderão vir diretamente do backend.
*/

const resumoMovimentacoes = ref({
  total: 12,
  emAberto: 2,
  pendente: 3,
  concluido: 4
})


/*
|--------------------------------------------------------------------------
| VERIFICA SE EXISTEM FILTROS ATIVOS
|--------------------------------------------------------------------------
| Essa propriedade será útil posteriormente caso queiramos
| mostrar chips com os filtros aplicados.
*/

// const possuiFiltros = computed(() => {
//   return (
//     filtros.value.busca ||
//     filtros.value.tipo ||
//     filtros.value.data
//   )
// })


/*
|--------------------------------------------------------------------------
| NOVA MOVIMENTAÇÃO
|--------------------------------------------------------------------------
| Por enquanto apenas deixamos a função preparada.
|
| Quando o formulário de cadastro existir, aqui poderemos:
| - abrir um Dialog;
| - navegar para outra página;
| - ou chamar um componente de cadastro.
*/

const novaMovimentacao = () => {
  console.log('Abrir cadastro de nova movimentação')
}
</script>